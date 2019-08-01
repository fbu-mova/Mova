package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.utils.GoalUtils;

public class ActionViewComponent extends ChecklistItemComponent<Action> {
        // needs a separate name for getName component manager, and to implement icons...

    private static final String TAG = "action view comp";

    private ComponentManager componentManager;

    public ActionViewComponent(Action action, ComponentManager componentManager) {
        super(action, Color.parseColor("#999999"), Color.parseColor("#222222"),
                false, (item) -> item.getTask(),
                (item) -> (item.getIsDone()));
        setManager(componentManager);
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

        // FIXME: Will this ever be called, given polymorphic structure? (adapters call the most generic component possible, I think)
        // todo -- implement icons
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
