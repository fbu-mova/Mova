package com.example.mova.components;

import android.app.Activity;
import android.util.Log;
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
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalCardComponent extends Component<Goal> {

    // fixme -- is there a diff between personal goal cards and social goal cards? social goals will be goals in a post?

    private static final String TAG = "goal card comp";
    private static final int viewLayoutRes = R.layout.item_goal_card;

    private View view;
    private GoalCardViewHolder viewHolder;
    private DelegatedResultActivity activity;

    // for action recyclerview in the card
    private ArrayList<Action> actions;
    private ComponentAdapter<Action> actionsAdapter;

    public GoalCardComponent(Goal item) {
        super(item);
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new GoalCardViewHolder(view);
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

        viewHolder.tvName.setText(item.getTitle());
        viewHolder.tvDescription.setText(item.getDescription());

        viewHolder.tvQuote.setVisibility(View.GONE); // fixme -- to include quotes
        viewHolder.tvNumDone.setVisibility(View.GONE); // fixme -- can add personal bool, alter accordingly
        viewHolder.tvTag.setVisibility(View.GONE); // todo -- include tag functionality

        // get and render the actions

        actions = new ArrayList<>();

        actionsAdapter = new ComponentAdapter<Action>(activity, actions) {
            @Override
            public Component<Action> makeComponent(Action item) {
                Component<Action> component = new ActionComponent(item);
                return component;
            }
        };

        viewHolder.rvActions.setLayoutManager(new LinearLayoutManager(activity));
        viewHolder.rvActions.setAdapter(actionsAdapter);

        loadGoalActions();
    }

    private void loadGoalActions() {
        // make query calls to get the user's actions for a goal
        ParseQuery<Action> actionQuery = item.relActions.getQuery();
        actionQuery.whereEqualTo("parentUser", (User) ParseUser.getCurrentUser());
        updateAdapter(actionQuery, actions, actionsAdapter, viewHolder.rvActions);
    }

    private void updateAdapter(ParseQuery<Action> actionQuery, ArrayList<Action> actions, ComponentAdapter<Action> actionsAdapter, RecyclerView rvActions) {

        actionQuery.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "query for actions successful!");

                    for (int i = 0; i < objects.size(); i++) {
                        // load into recyclerview
                        Action action = objects.get(i);
                        actions.add(0, action);
                        actionsAdapter.notifyItemInserted(0);
                    }

                    rvActions.scrollToPosition(0);
                }
                else {
                    Log.e(TAG, "query for actions failed", e);
                    Toast.makeText(activity, "Query for actions of your goal failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static class GoalCardViewHolder extends Component.ViewHolder {

        @BindView(R.id.pbProgress)      protected ProgressBar pbProgress; // todo -- later
        @BindView(R.id.tvQuote)         protected TextView tvQuote; // todo -- add to Parse ? stretch goal
        @BindView(R.id.tvName)          protected TextView tvName;
        @BindView(R.id.tvDescription)   protected TextView tvDescription;
        @BindView(R.id.rvActions)       protected RecyclerView rvActions; // todo -- action component to render in this class
        @BindView(R.id.tvNumDone)       protected TextView tvNumDone; // fixme -- in personal, only one person ?
        @BindView(R.id.tvTag)           protected TextView tvTag; // fixme -- what about multiple tags?

        public GoalCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
