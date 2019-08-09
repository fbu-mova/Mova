package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.icons.Icons;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.ColorUtils;
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
        Log.e(TAG, "viewholder not inflated");
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

        ColorUtils.Hue hue = goal.getHue();
        if (hue == null) hue = ColorUtils.Hue.random();
        int ultraLight = ColorUtils.getColor(getActivity().getResources(), hue, ColorUtils.Lightness.UltraLight);
        int mid = ColorUtils.getColor(getActivity().getResources(), hue, ColorUtils.Lightness.Mid);

        viewHolder.tvGoalTitle.setTextColor(mid);
        viewHolder.goalProgressBar.setUnfilledColor(ultraLight);
        viewHolder.goalProgressBar.setFilledColor(mid);
        Icons.from(getActivity()).displayNounIcon(goal, null, viewHolder.ivGoal);

        viewHolder.tvGoalTitle.setText(goal.getTitle());

        GoalUtils.getNumActionsComplete(goal, User.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            viewHolder.goalProgressBar.setProgress(progress);
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ProgressGoalViewHolder extends Component.ViewHolder{

        @BindView(R.id.tvGoalTitle) protected TextView tvGoalTitle;
        @BindView(R.id.ivGoal) protected ImageView ivGoal;
        @BindView(R.id.cvGoal) protected CardView cvGoal;
        @BindView(R.id.goalProgressBar) protected GoalProgressBar goalProgressBar;

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
