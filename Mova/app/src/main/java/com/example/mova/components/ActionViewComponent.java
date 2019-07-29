package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

    private static final int viewLayoutRes = R.layout.item_checklist;
    private static final String TAG = "action view comp";

    private Action action;
    private View view;
    private ActionViewViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ComponentManager componentManager;

    public ActionViewComponent(Action action, ComponentManager componentManager) {
        super(action, Color.parseColor("#999999"), Color.parseColor("#222222"),
                false, (item) -> item.getTask());
        this.action = action;
        setManager(componentManager);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        viewHolder = new ActionViewViewHolder(view);
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
        return "ActionViewComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
        viewHolder.cbItem.setText(action.getTask());
        viewHolder.cbItem.setOnClickListener((view) -> onClick(view));

        if (action.getIsDone()) {
            // need it to show up as checked
        }

        // todo -- implement icons
    }

    @Override
    public void onClick(View view) {
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

    public static class ActionViewViewHolder extends ChecklistItemComponent.ViewHolder {

//        @BindView(R.id.cbItem)      protected CheckBox cbItem;

        public ActionViewViewHolder(@NonNull View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
        }
    }
}
