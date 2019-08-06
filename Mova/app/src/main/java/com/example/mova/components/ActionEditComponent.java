package com.example.mova.components;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.model.Action;
import com.example.mova.model.Recurrence;
import com.example.mova.utils.GoalUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionEditComponent extends Component {

    private static final int viewLayoutRes = R.layout.item_action_edit;
    private static final String TAG = "action edit comp";

    private Action action;
    private ActionEditViewHolder viewHolder;

    private ComponentManager componentManager;

    private List<Component> recurComponents;
    private ComponentAdapter recurAdapter;

    public ActionEditComponent(Action action, ComponentManager componentManager) {
        super();
        this.action = action;
        setManager(componentManager);
    }

    @Override
    public ViewHolder getViewHolder() {
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public String getName() {
        return "ActionEditComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        checkViewHolderClass(holder, ActionEditViewHolder.class);
        this.viewHolder = (ActionEditViewHolder) holder;

        // todo -- implement icons (need to update in action model)

        viewHolder.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update this action with new text
                String new_action = viewHolder.etAction.getText().toString();

                // fixme -- add case where editing personal version of a social goal,
                //  so action saved, connected to SharedAction set to false, sharedAction not changed
                GoalUtils.saveSharedAndAction(action, new_action, (item) -> {
                    Toast.makeText(getActivity(), "Updated action", Toast.LENGTH_SHORT).show();
                });

                componentManager.swap("ActionViewComponent");
            }
        });

        viewHolder.ivRepeatButton.setOnClickListener((v) -> openRecurrenceSettings());
    }

    @Override
    protected void onDestroy() {

    }

    private void openRecurrenceSettings() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_recycler_view, null);
        RecyclerView rv = view.findViewById(R.id.rv);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Repeat this action...")
                .setView(view)
                .setPositiveButton("Save", (DialogInterface dialog, int which) -> updateRecurrences())
                .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> { });

        recurComponents = new ArrayList<>();
        recurAdapter = new ComponentAdapter(getActivity(), recurComponents);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(recurAdapter);
        rv.addItemDecoration(new EdgeDecorator(32));

        recurComponents.add(new SimpleListAddComponent() {
            @Override
            protected void onClick(View view) {
                int secondToLast = recurComponents.size() - 1;
                recurComponents.add(secondToLast, new RecurrenceSettingsComponent() {
                    @Override
                    protected void onClose(RecurrenceSettingsComponent component) {
                        recurComponents.remove(component);
                        recurAdapter.notifyWithFlush(rv);
                    }
                });
                recurAdapter.notifyWithFlush(rv);
            }
        });
        recurAdapter.notifyItemInserted(0);

        builder.show();
    }

    private void updateRecurrences() {
        List<Recurrence> recurrence = new ArrayList<>();
        for (Component component : recurComponents) {
            try {
                RecurrenceSettingsComponent recurComponent = (RecurrenceSettingsComponent) component;
                Recurrence r = recurComponent.makeRecurrence();
                recurrence.add(r);
            } catch (ClassCastException e) {
                Log.d("ActionEditComponent", "Skip non-recurrence component");
            }
        }
        action.setRecurrence(recurrence);
        // FIXME: Might this disturb empty or shared recurrences?
    }

    public static class ActionEditViewHolder extends Component.ViewHolder {

        @BindView(R.id.etAction)         protected EditText etAction;
        @BindView(R.id.ivReminderButton) protected ImageView ivReminderButton; // fixme -- in future, image buttons
        @BindView(R.id.ivRepeatButton)   protected ImageView ivRepeatButton;
        @BindView(R.id.ivPriorityButton) protected ImageView ivPriorityButton;
        @BindView(R.id.btSave)           protected Button btSave;

        public ActionEditViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ActionEditViewHolder(view);
        }
    }
}
