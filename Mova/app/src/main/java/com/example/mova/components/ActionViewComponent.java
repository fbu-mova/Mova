package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.utils.GoalUtils;

public class ActionViewComponent extends ChecklistItemComponent<Action> {
        // needs a separate name for getName component manager, and to implement icons...
        // fixme -- needs diff viewLayoutRes to implement icons

    private static final String TAG = "action view comp";
    private static final int viewLayoutRes = R.layout.item_checklist;

    private Action action;


    private ChecklistItemComponent.ViewHolder viewHolder;

    private ComponentManager componentManager;

    public ActionViewComponent(Action action, ComponentManager componentManager) {
        super(action, Color.parseColor("#999999"), Color.parseColor("#222222"),
                false, (item) -> item.getTask(),
                (item) -> (item.getIsDone()));
        this.action = action;
        setManager(componentManager);
    }

    @Override
    public ViewHolder getViewHolder() {
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public String getName() {
        return "ActionViewComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        super.onRender(holder);
        checkViewHolderClass(holder, ChecklistItemComponent.ViewHolder.class);


        this.viewHolder = (ViewHolder) holder;


        // FIXME: Will this ever be called, given polymorphic structure? (adapters call the most generic component possible, I think)
        // todo -- implement icons

        if (action.getIsPriority()) {
            // set icon next to checklist item
            viewHolder.ivPriority.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        GoalUtils.toggleDone(item, (e) -> {
            if (e == null) {
                Log.d(TAG, "toggled action done");
            }
            else {
                Log.e(TAG, "toggled action failed", e);
                Toast.makeText(getActivity(), "Toggling action failed", Toast.LENGTH_LONG).show();
            }
        });
    }


}
