package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class TomorrowFocusPromptComponent extends Component {

    public class BelowMinimumGoalsError extends Error {
        public BelowMinimumGoalsError() {
            super();
        }

        public BelowMinimumGoalsError(String message) {
            super(message);
        }
    }

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private DataComponentAdapter<Goal> adapter;
    private List<Goal> goals;
    private int minGoals, maxGoals;

    private ComponentManager componentManager;

    public TomorrowFocusPromptComponent() {
        this.minGoals = 3;
        this.maxGoals = 5;
        loadGoals();
    }

    public TomorrowFocusPromptComponent(int minGoals, int maxGoals) {
        this.minGoals = minGoals;
        this.maxGoals = maxGoals;
        loadGoals();
        // TODO: Add bottom portion with search for other goals
    }

    private void loadGoals() {
        ParseQuery<Goal> query = ((User) User.getCurrentUser()).relGoals.getQuery();
        query.orderByDescending(Goal.KEY_CREATED_AT);
        query.setLimit(maxGoals);
        query.findInBackground((goals, e) -> {
            if (e != null) {
                Log.e("TomorrowFocusComponent", "Failed to load goals", e);
                onLoadGoals(e);
            } else if (goals.size() < minGoals) {
                BelowMinimumGoalsError belowMinErr = new BelowMinimumGoalsError("The number of goals loaded is below the minimum number allowed.");
                onLoadGoals(belowMinErr);
            } else {
                this.goals = goals;
                onLoadGoals(null);
            }
        });
    }

    public abstract void onLoadGoals(Throwable e);

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        this.activity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.component_tomorrow_focus_prompt, parent, attachToRoot);
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
        adapter = new DataComponentAdapter<Goal>(activity, goals) {
            @Override
            public Component makeComponent(Goal item) {
                return new ChecklistItemComponent<Goal>(item,
                        Color.parseColor("#FFFFFF"), Color.parseColor("#C9DBFF"), true,
                        (o) -> o.getTitle()) {
                    @Override
                    public void onClick(View view) {
                        // TODO: Prevent more than three goals from being selected at any given time
                    }
                };
            }
        };
        holder.rvGoals.setAdapter(adapter);
        holder.rvGoals.setLayoutManager(new LinearLayoutManager(activity));
    }

    public class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvHeader)    public TextView tvHeader;
        @BindView(R.id.tvSubheader) public TextView tvSubheader;
        @BindView(R.id.rvGoals)     public RecyclerView rvGoals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public String getName() {
        return "TomorrowFocusPromptComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }
}
