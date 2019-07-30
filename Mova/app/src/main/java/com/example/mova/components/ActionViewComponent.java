package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Action;
import com.example.mova.utils.GoalUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionViewComponent extends ChecklistItemComponent<Action> {
        // needs a separate name for getName component manager, and to implement icons...

    private static final String TAG = "action view comp";

    private ComponentManager componentManager;

    public ActionViewComponent(Action action, ComponentManager componentManager) {
        super(action, Color.parseColor("#999999"), Color.parseColor("#222222"),
                false, (item) -> item.getTask(),
                (item) -> (item.getIsDone()));
        this.item = action;
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
    public void render() {
        super.render();

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
                Toast.makeText(activity, "Toggling action failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}
