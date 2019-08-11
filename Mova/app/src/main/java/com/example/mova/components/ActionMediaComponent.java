package com.example.mova.components;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.R;
import com.example.mova.component.Component;
import com.example.mova.icons.Icons;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActionMediaComponent extends ChecklistItemComponent<Action> {

    private Goal goal;

    public ActionMediaComponent(Goal goal, Action action) {
        super(action, item -> item.getTask(), item -> item.getIsDone());
        this.goal = goal;
    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        super.onRender(holder);

        checkViewHolderClass(holder, ViewHolder.class);
        ViewHolder xHolder = (ViewHolder) holder;

        xHolder.tvGoal.setText(goal.getTitle());
        Icons.from(getActivity()).displayNounIcon(goal, xHolder.cvGoal, xHolder.ivGoal);
    }

    @Override
    public void onCheckedChanged(boolean isChecked) {

    }

    public class ViewHolder extends ChecklistItemComponent.ViewHolder {

        @BindView(R.id.llGoal) protected LinearLayout llGoal;
        @BindView(R.id.cvGoal) protected CardView cvGoal;
        @BindView(R.id.ivGoal) protected ImageView ivGoal;
        @BindView(R.id.tvGoal) protected TextView tvGoal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            avItem.setEnabled(false);
        }
    }

    public class Inflater extends ChecklistItemComponent.Inflater {

    }
}
