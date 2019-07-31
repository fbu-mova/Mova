package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Action;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.parse.ParseException;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionEditComponent extends Component {

    private static final int viewLayoutRes = R.layout.item_action_edit;
    private static final String TAG = "action edit comp";

    private Action action;
    private View view;
    private ActionEditViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ComponentManager componentManager;

    public ActionEditComponent(Action action, ComponentManager componentManager) {
        super();
        this.action = action;
        setManager(componentManager);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        viewHolder = new ActionEditViewHolder(view);
        this.activity = activity;
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
    public View getView() {
        return view;
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
    public void render() {

        // todo -- implement icons (need to update in action model)

        viewHolder.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update this action with new text
                String new_action = viewHolder.etAction.getText().toString();

                // fixme -- add case where editing personal version of a social goal,
                //  so action saved, connected to SharedAction set to false, sharedAction not changed
                GoalUtils.saveSharedAndAction(action, new_action, (item) -> {
                    Toast.makeText(activity, "Updated action", Toast.LENGTH_SHORT).show();
                });

                componentManager.swap("ActionViewComponent");
            }
        });
    }

    public static class ActionEditViewHolder extends Component.ViewHolder {

        @BindView(R.id.etAction)        protected EditText etAction;
        @BindView(R.id.ivIcon1)         protected ImageView ivIcon1; // fixme -- in future, image buttons
        @BindView(R.id.ivIcon2)         protected ImageView ivIcon2;
        @BindView(R.id.ivIcon3)         protected ImageView ivIcon3;
        @BindView(R.id.btSave)        protected Button btSave;

        public ActionEditViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
