package com.example.mova.components;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Goal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalThumbnailComponent extends Component<Goal> {

    // todo -- overall scroll method (insert scrollview)

    private static final String TAG = "goal thumbnail comp'nt";
    private static final int viewLayoutRes = R.layout.item_goal_thumbnail_card;

    private View view;
    private GoalThumbnailViewHolder viewHolder;
    private DelegatedResultActivity activity;

    public GoalThumbnailComponent(Goal item) {
        super(item);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
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
    public void render() {
        if (viewHolder == null) {
            Log.e(TAG, "not inflating views to viewHolder, in render");
            return;
        }

        Goal goal = getItem();

        viewHolder.tvName.setText(goal.getTitle());

        // fixme -- does there exist cleaner code to do this casework? / extract as helper function?
        String name = goal.getFromGroupName();
        if (name != "") {
            viewHolder.tvFromGroup.setText(name);
        }
        else {
            viewHolder.tvFromGroup.setVisibility(View.GONE);
        }


        // how to get context for binding glide images? -- made it a field

        String url = (goal.getImage() != null) ? goal.getImage().getUrl() : "";
        Glide.with(activity)
                .load(url)
                .error(R.drawable.clock) // todo - replace to be better image, add rounded corners
                .placeholder(R.drawable.clock)
                .into(viewHolder.ivPhoto);

        // todo: progressbar

    }

    public static class GoalThumbnailViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvFromGroup) protected TextView tvFromGroup;
        @BindView(R.id.tvName)      protected TextView tvName;
        @BindView(R.id.ivPhoto)     protected ImageView ivPhoto;
        @BindView(R.id.pbProgress)  protected ProgressBar pbProgress;

        public GoalThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
