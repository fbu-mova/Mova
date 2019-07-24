package com.example.mova.components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalThumbnailComponent extends Component {

    // todo -- overall scroll method (insert scrollview)

    private static final String TAG = "goal thumbnail comp'nt";
    private static final int viewLayoutRes = R.layout.item_goal_thumbnail_card;

    private Goal goal;
    private View view;
    private GoalThumbnailViewHolder viewHolder;
    private DelegatedResultActivity activity;

    private ComponentManager componentManager;

    public GoalThumbnailComponent(Goal goal) {
        super();
        this.goal = goal;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new GoalThumbnailViewHolder(view);
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
        return "GoalThumbnailComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public void render() {
        if (viewHolder == null) {
            Log.e(TAG, "not inflating views to viewHolder, in render");
            return;
        }

        viewHolder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, GoalDetailsActivity.class);
                intent.putExtra("goal", goal);

                // fixme -- add ability to alter priority of goals as go back to goals fragment

                activity.startActivity(intent);
            }
        });

        viewHolder.tvName.setText(goal.getTitle());

        viewHolder.tvFromGroup.setText(goal.getGroupName());

        // how to get context for binding glide images? -- made it a field

        String url = (goal.getImage() != null) ? goal.getImage().getUrl() : "";
        Glide.with(activity)
                .load(url)
                .error(R.color.colorAccent) // todo - replace to be better image, add rounded corners
                .placeholder(R.color.colorAccent)
                .into(viewHolder.ivPhoto);

        // todo: progressbar

    }

    public static class GoalThumbnailViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvFromGroup) protected TextView tvFromGroup;
        @BindView(R.id.tvName)      protected TextView tvName;
        @BindView(R.id.ivPhoto)     protected ImageView ivPhoto;
        @BindView(R.id.pbProgress)  protected ProgressBar pbProgress;
        @BindView(R.id.constraintLayout)    protected ConstraintLayout clLayout;

        public GoalThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
