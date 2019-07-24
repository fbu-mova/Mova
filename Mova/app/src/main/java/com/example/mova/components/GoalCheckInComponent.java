package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalCheckInComponent extends Component {

    private Goal goal;
    private String message;

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;

    private DataComponentAdapter<Action> adapter;

    public GoalCheckInComponent(Goal goal, String message) {
        super();
        this.goal = goal;
        this.message = message;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_goal_check_in, parent, attachToRoot);
        holder = new ViewHolder(view);
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void render() {
        // Display goal title and message
        holder.tvGoalTitle.setText(goal.getTitle());
        holder.tvSubheader.setText(message);

        // Retrieve actions
        ParseQuery<Action> actionsQuery = goal.relActions.getQuery();
        actionsQuery.findInBackground((actions, e) -> {
            // Set up actions checklist
            adapter = new DataComponentAdapter<Action>(activity, actions) {
                @Override
                public Component makeComponent(Action item) {
                    return new ChecklistItemComponent<Action>(item,
                            Color.parseColor("#999999"), Color.parseColor("#222222"), false,
                            (action) -> action.getTask()) {
                        @Override
                        public void onClick(View view) {
                            toggleDone(item, (e) -> {
                                if (e != null) {
                                    Log.e("GoalCheckInComponent", "Failed to toggle action done", e);
                                    Toast.makeText(activity, "Failed to toggle action done", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.i("GoalCheckInComponent", "Toggled action done");
                                }
                            });
                        }
                    };
                }
            };
            holder.rvChecklist.setAdapter(adapter);
            holder.rvChecklist.setLayoutManager(new LinearLayoutManager(activity));

            // Display progress
            holder.pbProgress.setProgress(getProgressPercent(actions));

            // TODO: Add onclick listener for whole card to open the goal detail view for the associated goal
        });
    }


    // TODO: Merge this functionality with functionality in GoalUtils
    private static int numActionsComplete(List<Action> actions) {
        int completed = 0;
        for (Action action : actions) {
            completed += (action.getIsDone()) ? 1 : 0;
        }
        return completed;
    }

    // TODO: Merge this functionality with functionality in GoalUtils
    private static int getProgressPercent(List<Action> actions) {
        int numComplete = numActionsComplete(actions);
        int percent = (int) Math.floor(100
                * (   ((double) numComplete)
                    / ((double) actions.size())
                  ));
        return percent;
    }

    // TODO: Merge this functionality with functionality in GoalUtils
    private static void toggleDone(Action action, AsyncUtils.ItemCallback<Throwable> callback) {
        action.setIsDone(!action.getIsDone());
        action.saveInBackground((e) -> callback.call(e));
    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.pbProgress)  public ProgressBar pbProgress;
        @BindView(R.id.tvGoalTitle) public TextView tvGoalTitle;
        @BindView(R.id.tvSubheader) public TextView tvSubheader;
        @BindView(R.id.rvChecklist) public RecyclerView rvChecklist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}