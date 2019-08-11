package com.example.mova.components;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.Wrapper;
import com.example.mova.views.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.icons.Icons;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static com.example.mova.views.GoalProgressBar.PROGRESS_MAX;

public class GoalThumbnailComponent extends Component {

    // todo -- overall scroll method (insert scrollview)

    private static final String TAG = "goal thumbnail comp'nt";
    private static final int viewLayoutRes = R.layout.item_goal_thumbnail_card;

    private Goal goal;
    private boolean userIsInvolved;
    private ViewHolder viewHolder;
    private Wrapper<Boolean> showGroup;

    private ComponentManager componentManager;

    public GoalThumbnailComponent(Goal.GoalData bundle) {
        this(bundle, new Wrapper<>(true));
    }

    public GoalThumbnailComponent(Goal.GoalData bundle, Wrapper<Boolean> showGroup) {
        super();
        this.goal = bundle.goal;
        this.userIsInvolved = bundle.userIsInvolved;
        this.showGroup = showGroup;
    }

    @Override
    public Component.ViewHolder getViewHolder() {
//        viewHolder = new ViewHolder(view);
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
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
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        viewHolder = (ViewHolder) holder;

        viewHolder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalDetailsActivity.class);
                intent.putExtra("goal", goal);
                intent.putExtra("isUserInvolved", userIsInvolved);

                // fixme -- add ability to alter priority of goals as go back to goals fragment

                getActivity().startActivity(intent);
            }
        });

        viewHolder.tvName.setText(goal.getTitle());

        goal.getGroupName(() -> {
            if (showGroup.item) {
                viewHolder.tvFromGroup.setVisibility(View.INVISIBLE);
                viewHolder.cvFromGroupIcon.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.tvFromGroup.setVisibility(View.GONE);
                viewHolder.cvFromGroupIcon.setVisibility(View.GONE);
            }
        }, (str) -> {
            int visibility;
            if (!showGroup.item) {
                visibility = View.GONE;
            } else {
                visibility = (str.equals("")) ? View.INVISIBLE : View.VISIBLE;
            }

            viewHolder.tvFromGroup.setText(str);
            viewHolder.tvFromGroup.setVisibility(visibility);
            viewHolder.cvFromGroupIcon.setVisibility(visibility);

            if (visibility == VISIBLE) {
                Icons.from(getActivity()).displayNounIcon(goal.getGroup(), viewHolder.cvFromGroupIcon, viewHolder.ivFromGroupIcon);
            }
        });

        Icons.from(getActivity()).displayNounIcon(goal, null, viewHolder.ivPhoto);

        Resources res = getActivity().getResources();
        viewHolder.goalProgressBar.setUnfilledColor(ColorUtils.getColor(res, goal.getHue(), ColorUtils.Lightness.UltraLight));
        viewHolder.goalProgressBar.setFilledColor(ColorUtils.getColor(res, goal.getHue(), ColorUtils.Lightness.Mid));

        GoalUtils.getNumActionsComplete(goal, User.getCurrentUser(), (numDone, numTotal) -> {
            float percent = (float) numDone / (float) numTotal;
            int progress = (int) (percent * PROGRESS_MAX);
            viewHolder.goalProgressBar.setProgress(progress);
        });
    }

    @Override
    protected void onDestroy() {

    }

    public void setProgress(int progress) {
        viewHolder.goalProgressBar.setProgress(progress);
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.llRoot)              public LinearLayout llRoot;
        @BindView(R.id.tvFromGroup)         protected TextView tvFromGroup;
        @BindView(R.id.tvName)              protected TextView tvName;
        @BindView(R.id.ivIcon)              protected ImageView ivPhoto;
        @BindView(R.id.goalProgressBar)     protected GoalProgressBar goalProgressBar;
        @BindView(R.id.cvFromGroupIcon)     protected CardView cvFromGroupIcon;
        @BindView(R.id.ivFromGroupIcon)     protected ImageView ivFromGroupIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }

}
