package com.example.mova.components;

import android.app.Activity;
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
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.model.Goal;
import com.example.mova.utils.AsyncUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class TomorrowFocusPromptComponent extends Component {

    private DelegatedResultActivity activity;
    private ViewHolder holder;
    private View view;
    private ComponentAdapter<Goal> adapter;
    private List<Goal> goals;

    public TomorrowFocusPromptComponent() {
        Goal.Query query = new Goal.Query();
        query.orderByDescending(Goal.KEY_CREATED_AT);
        query.getTop(5);
        query.findInBackground((goals, e) -> {
            if (e != null) {
                Log.e("TomorrowFocusComponent", "Failed to get top goals", e);
                finishInit(e);
            } else {
                this.goals = goals;
                finishInit(null);
            }
        });
    }

    public abstract void finishInit(Throwable e);

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
        adapter = new ComponentAdapter<Goal>(activity, goals) {
            @Override
            public Component<Goal> makeComponent(Goal item) {
                return new ChecklistItemComponent<Goal>(item,
                        Color.parseColor("#FFFFFF"), Color.parseColor("#C9DBFF"),
                        (o) -> o.getTitle()) {
                    @Override
                    public void onClick(View view) {

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
}
