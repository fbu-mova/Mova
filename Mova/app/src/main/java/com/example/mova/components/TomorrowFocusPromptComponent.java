package com.example.mova.components;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
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

    private ViewHolder holder;
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
        ParseQuery<Goal> query = User.getCurrentUser().relGoals.getQuery();
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
    public String getName() {
        return "TomorrowFocusPromptComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        adapter = new DataComponentAdapter<Goal>(getActivity(), goals) {
            @Override
            protected Component makeComponent(Goal item, Component.ViewHolder holder) {
                return new ChecklistItemComponent<Goal>(item,
                        Color.parseColor("#FFFFFF"), Color.parseColor("#C9DBFF"), true,
                        (o) -> o.getTitle(), (o) -> false) { // fixme - made goals always not done
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // TODO: Prevent more than three goals from being selected at any given time
                    }
                };
            }

            @Override
            protected Component.Inflater makeInflater(Goal item) {
                return null;
            }
        };
        this.holder.rvGoals.setAdapter(adapter);
        this.holder.rvGoals.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.tvHeader)    public TextView tvHeader;
        @BindView(R.id.tvSubheader) public TextView tvSubheader;
        @BindView(R.id.rvGoals)     public RecyclerView rvGoals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_tomorrow_focus_prompt, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
