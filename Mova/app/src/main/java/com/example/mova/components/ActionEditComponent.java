package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
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
    private ActionEditViewHolder viewHolder;

    private ComponentManager componentManager;
    private GoalUtils.onActionEditSaveListener onActionEditSaveListener;

    public ActionEditComponent(Action action, ComponentManager componentManager, GoalUtils.onActionEditSaveListener onActionEditSaveListener) {
        super();
        this.action = action;
        setManager(componentManager);
        this.onActionEditSaveListener = onActionEditSaveListener;
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

        viewHolder.btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onActionEditSaveListener.call(viewHolder.etAction.getText().toString(), componentManager);

            }
        });

        // todo -- implement icons (need to update in action model)

        viewHolder.recurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to update the uncreatedAction with priority

                action.setIsPriority(true);
                // fixme -- want to show onClick in UI : what would user see?
            }
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ActionEditViewHolder extends Component.ViewHolder {

        @BindView(R.id.etAction)        protected EditText etAction;
        @BindView(R.id.ivIcon1)         protected ImageView recurring;
        @BindView(R.id.ivIcon2)         protected ImageView reminder;
        @BindView(R.id.ivIcon3)         protected ImageView priority;
        @BindView(R.id.btSave)          protected Button btSave;

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
