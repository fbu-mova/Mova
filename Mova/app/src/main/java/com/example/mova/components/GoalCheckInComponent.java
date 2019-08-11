package com.example.mova.components;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.utils.ColorUtils;
import com.example.mova.views.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.model.Action.KEY_PARENT_USER;

public class GoalCheckInComponent extends Component {

    private Goal goal;
    private String message;

    // Async data
    private List<Action> goalActions;

    private ViewHolder holder;

    private DataComponentAdapter<Action> adapter;

    private ComponentManager componentManager;

    public GoalCheckInComponent(Goal goal, String message) {
        super();
        this.goal = goal;
        this.message = message;
        this.goalActions = new ArrayList<>();
    }

    public void loadData(AsyncUtils.ItemCallback<Throwable> callback) {

        ParseQuery<Action> actionsQuery = goal.relActions.getQuery();
        actionsQuery.whereEqualTo(KEY_PARENT_USER, User.getCurrentUser())
            .orderByAscending(Action.KEY_COMPLETED_AT);
        actionsQuery.findInBackground((actions, e) -> {
            this.goalActions = actions;
            callback.call(e);
        });
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    public String getName() {
        return "GoalCheckInComponent";
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
        this.holder = (ViewHolder) holder;

        // Display goal title and message
        this.holder.tvGoalTitle.setText(goal.getTitle());
        this.holder.tvSubheader.setText(message);

        Resources res = getActivity().getResources();
        this.holder.pbProgress.setUnfilledColor(ColorUtils.getColor(res, goal.getHue(), ColorUtils.Lightness.UltraLight));
        this.holder.pbProgress.setFilledColor(ColorUtils.getColor(res, goal.getHue(), ColorUtils.Lightness.Mid));

        // Set up actions checklist
        adapter = new DataComponentAdapter<Action>(getActivity(), goalActions) {

            @Override
            protected Component makeComponent(Action item, Component.ViewHolder holder) {
                return new ChecklistItemComponent<Action>(item,
                    Color.parseColor("#999999"), Color.parseColor("#222222"), false,
                    (action) -> action.getTask(), (action) -> action.getIsDone()) {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            GoalUtils.toggleDone(item, (e) -> {
                                if (e != null) {
                                    Log.e("GoalCheckInComponent", "Failed to toggle action done", e);
                                    Toast.makeText(getActivity(), "Failed to toggle action done", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.i("GoalCheckInComponent", "Toggled action done");
                                }
                            });
                        }
                };
            }

            @Override
            protected Component.Inflater makeInflater(Action item) {
                return new ChecklistItemComponent.Inflater();
            }
        };
        this.holder.rvChecklist.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.holder.rvChecklist.setAdapter(adapter);

        // Display progress
        this.holder.pbProgress.setProgress(GoalUtils.getProgressPercent(goalActions));

        // TODO: Add onclick listener for whole card to open the goal detail view for the associated goal
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.pbProgress)  public GoalProgressBar pbProgress;
        @BindView(R.id.tvGoalTitle) public TextView tvGoalTitle;
        @BindView(R.id.tvSubheader) public TextView tvSubheader;
        @BindView(R.id.rvChecklist) public RecyclerView rvChecklist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_goal_check_in, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
