package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.GoalProgressBar.PROGRESS_MAX;

public class ProgressGoalComponent extends Component {

    private static final String TAG = "ProgressGoalComponent";
    private static final int viewLayoutRes = R.layout.item_progress_goal;

    private Goal goal;
    private ProgressGoalViewHolder viewHolder;

    private ComponentManager componentManager;

    public ProgressGoalComponent(Goal item){
        super();
        this.goal = item;
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new ProgressGoalViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    public String getName() {
        return "ProgressGoalComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        checkViewHolderClass(holder, ProgressGoalViewHolder.class);
        viewHolder = (ProgressGoalViewHolder) holder;

//        if(viewHolder == null){
//            Log.e(TAG, "Not inflating views in render");
//            return;
//        }

        if(goal.getColor() != null){
            int color = Color.parseColor(goal.getColor());
            viewHolder.ivGoalColor.setColorFilter(color);
            viewHolder.goalProgressBar.setFilledColor(color);
            viewHolder.tvGoalTitle.setTextColor(color);
        }

        GoalUtils.getNumActionsComplete(goal, User.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            viewHolder.goalProgressBar.setProgress(viewHolder.goalProgressBar.getProgress(), progress);
        });

        viewHolder.tvGoalTitle.setText(goal.getTitle());
    }

    @Override
    protected void onDestroy() {

    }

    public static class ProgressGoalViewHolder extends Component.ViewHolder{

        @BindView(R.id.tvGoalTitle) protected TextView tvGoalTitle;
        @BindView(R.id.ivGoalColor) protected ImageView ivGoalColor;
        @BindView(R.id.goalProgressBar2) protected GoalProgressBar goalProgressBar;

        public ProgressGoalViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ProgressGoalViewHolder(view);
        }
    }
}
