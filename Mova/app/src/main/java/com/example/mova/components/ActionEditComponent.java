package com.example.mova.components;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.utils.GoalUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionEditComponent extends Component {

    private static final int viewLayoutRes = R.layout.item_action_edit;
    private static final String TAG = "action edit comp";

    private Action action;
    private Action.Wrapper wrapper;
    private ActionEditViewHolder viewHolder;

    private ComponentManager componentManager;
    private GoalUtils.onActionEditSaveListener onActionEditSaveListener;

    public ActionEditComponent(Action action, ComponentManager componentManager, GoalUtils.onActionEditSaveListener onActionEditSaveListener) {
        super();

        this.action = action;
        setManager(componentManager);
        this.onActionEditSaveListener = onActionEditSaveListener;

        this.wrapper = new Action.Wrapper();
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

        viewHolder.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wrapper.setMessage(viewHolder.etAction.getText().toString());
                onActionEditSaveListener.call(action, wrapper, componentManager);
                wrapper = new Action.Wrapper();
            }
        });

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentManager.swap("CreateActionViewComponent");
            }
        });

        // todo -- implement icons (need to update in action model)

        viewHolder.recurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentManager.swap("CreateActionViewComponent");
            }
        });

        viewHolder.priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to update the uncreatedAction with priority

                if (action != null) {
                    action.setIsPriority(true);
                }
                else {
                    // some action wrapper class that stores the info ?
                    wrapper.setIsPriority(true);
                }

                Toast.makeText(getActivity(), "priority selected!", Toast.LENGTH_LONG).show();
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
        @BindView(R.id.ivIcon2)         protected ImageView cancel;
        @BindView(R.id.ivIcon3)         protected ImageView priority;
        @BindView(R.id.ivSave)          protected ImageView ivSave;
        @BindView(R.id.parentLayout)    protected LinearLayout parentLayout;

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
