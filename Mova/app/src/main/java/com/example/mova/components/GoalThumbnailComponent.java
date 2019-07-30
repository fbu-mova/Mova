package com.example.mova.components;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.GoalProgressBar.PROGRESS_MAX;

public class GoalThumbnailComponent extends Component {

    // todo -- overall scroll method (insert scrollview)

    private static final String TAG = "goal thumbnail comp'nt";
    private static final int viewLayoutRes = R.layout.item_goal_thumbnail_card;

    private Goal goal;
    private GoalThumbnailViewHolder viewHolder;

    private ComponentManager componentManager;

    public GoalThumbnailComponent(Goal goal) {
        super();
        this.goal = goal;
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new GoalThumbnailViewHolder(view);
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public String getName() {
        return "GoalThumbnailComponent";
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
        checkViewHolderClass(holder, GoalThumbnailViewHolder.class);
        viewHolder = (GoalThumbnailViewHolder) holder;

//        if (viewHolder == null) {
//            Log.e(TAG, "not inflating views to viewHolder, in render");
//            return;
//        }

        viewHolder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalDetailsActivity.class);
                intent.putExtra("goal", goal);

                // fixme -- add ability to alter priority of goals as go back to goals fragment

                getActivity().startActivity(intent);
            }
        });

        viewHolder.tvName.setText(goal.getTitle());

        viewHolder.tvFromGroup.setText(goal.getGroupName());

        // how to get context for binding glide images? -- made it a field

        String url = (goal.getImage() != null) ? goal.getImage().getUrl() : "";
        Glide.with(getActivity())
                .load(url)
                .error(R.color.colorAccent) // todo - replace to be better image, add rounded corners
                .placeholder(R.color.colorAccent)
                .into(viewHolder.ivPhoto);

        GoalUtils.getNumActionsComplete(goal, User.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            viewHolder.goalProgressBar.setProgress(progress);
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class GoalThumbnailViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvFromGroup)         protected TextView tvFromGroup;
        @BindView(R.id.tvName)              protected TextView tvName;
        @BindView(R.id.ivPhoto)             protected ImageView ivPhoto;
        @BindView(R.id.goalProgressBar)     protected GoalProgressBar goalProgressBar;
        @BindView(R.id.constraintLayout)    protected ConstraintLayout clLayout;

        public GoalThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new GoalThumbnailViewHolder(view);
        }
    }

}
