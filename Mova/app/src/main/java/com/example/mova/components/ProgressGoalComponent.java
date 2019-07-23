package com.example.mova.components;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.R;
import com.example.mova.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressGoalComponent extends Component<Goal> {

    private static final String TAG = "ProgressGoalComponent";
    private static final int viewLayoutRes = R.layout.item_progress_goal;

    private View view;
    private ProgressGoalViewHolder viewHolder;
    private Activity activity;

    public ProgressGoalComponent(Goal item){
        super(item);
    }


    @Override
    public void makeViewHolder(Activity activity, ViewGroup parent) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new ProgressGoalViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public void render() {
        if(viewHolder == null){
            Log.e(TAG, "Not inflating views in render");
            return;
        }

        Goal goal = getItem();

        if(goal.getColor() != null){
            viewHolder.ivGoalColor.setColorFilter(Color.parseColor(goal.getColor()));
        }
        viewHolder.tvGoalTitle.setText(goal.getTitle());
    }

    public static class ProgressGoalViewHolder extends Component.ViewHolder{

        @BindView(R.id.tvGoalTitle) protected TextView tvGoalTitle;
        @BindView(R.id.ivGoalColor) protected ImageView ivGoalColor;

        public ProgressGoalViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
