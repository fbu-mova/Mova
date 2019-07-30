package com.example.mova.components;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.example.mova.GoalProgressBar.PROGRESS_MAX;
import static com.example.mova.activities.GoalComposeActivity.REQUEST_GOAL_DETAILS;

public class GoalCardComponent extends Component {

    // fixme -- is there a diff between personal goal cards and social goal cards? social goals will be goals in a post?

    private static final String TAG = "goal card comp";
    private static final int viewLayoutRes = R.layout.item_goal_card;

    private Goal item;
    private View view;
    private GoalCardViewHolder viewHolder;
    private DelegatedResultActivity activity;

    // for action recyclerview in the card
    private ArrayList<Action> actions;
    private DataComponentAdapter<Action> actionsAdapter;

    private ComponentManager componentManager;

    public GoalCardComponent(Goal item) {
        super();
        this.item = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
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
    public View getView() {
        return view;
    }

    @Override
    public String getName() {
        return "GoalCardComponent";
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

        Log.d(TAG, "in render function");

        viewHolder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, GoalDetailsActivity.class);
                intent.putExtra("goal", item);

                // fixme -- add ability to alter priority of goals as go back to goals fragment

                activity.startActivity(intent);

//                activity.startActivityForDelegatedResult(intent, REQUEST_GOAL_DETAILS, new DelegatedResultActivity.ActivityResultCallback() {
//                    @Override
//                    public void call(int requestCode, int resultCode, Intent data) {
//                        if (resultCode == RESULT_OK) {
//                            if (requestCode == REQUEST_GOAL_DETAILS) {
//
//                            }
//                        }
//                    }
//                });
            }
        });

        viewHolder.tvName.setText(item.getTitle());
        Log.d(TAG, String.format("tvName of this viewholder: %s", viewHolder.tvName.getText().toString()));

        viewHolder.tvDescription.setText(item.getDescription());
        Log.d(TAG, String.format("tvDescription of this viewholder: %s", viewHolder.tvDescription.getText().toString()));

        GoalUtils.getNumActionsComplete(item, User.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            viewHolder.goalProgressBar.setProgress(progress);
        });

        viewHolder.tvQuote.setVisibility(View.GONE); // fixme -- to include quotes
        viewHolder.tvNumDone.setVisibility(View.GONE); // fixme -- can add personal bool, alter accordingly
        viewHolder.tvTag.setVisibility(View.GONE); // todo -- include tag functionality

        // get and render the actions

        actions = new ArrayList<>();

        actionsAdapter = new DataComponentAdapter<Action>(activity, actions) {
            @Override
            public Component makeComponent(Action item) {
                Component component = new ActionComponent(item);
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
        actionQuery.whereEqualTo("parentUser", User.getCurrentUser());
        updateAdapter(actionQuery, actions, actionsAdapter, viewHolder.rvActions);
    }

    private void updateAdapter(ParseQuery<Action> actionQuery, ArrayList<Action> actions, DataComponentAdapter<Action> actionsAdapter, RecyclerView rvActions) {

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

        @BindView(R.id.goalProgressBar) protected GoalProgressBar goalProgressBar;
        @BindView(R.id.tvQuote)         protected TextView tvQuote; // todo -- add to Parse ? stretch goal
        @BindView(R.id.tvName)          protected TextView tvName;
        @BindView(R.id.tvDescription)   protected TextView tvDescription;
        @BindView(R.id.rvActions)       protected RecyclerView rvActions;
        @BindView(R.id.tvNumDone)       protected TextView tvNumDone; // fixme -- in personal, only one person ?
        @BindView(R.id.tvTag)           protected TextView tvTag; // fixme -- what about multiple tags?
        @BindView(R.id.layout)          protected ConstraintLayout clLayout;

        public GoalCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
