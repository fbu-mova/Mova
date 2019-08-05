package com.example.mova.components;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.GoalProgressBar.PROGRESS_MAX;
import static com.example.mova.activities.GoalComposeActivity.REQUEST_GOAL_DETAILS;
import static com.example.mova.model.Action.KEY_PARENT_USER;
import static com.example.mova.model.User.getCurrentUser;

public class GoalCardComponent extends Component {

    // fixme -- is there a diff between personal goal cards and social goal cards? social goals will be goals in a post?

    private static final String TAG = "goal card comp";
    private static final int viewLayoutRes = R.layout.item_goal_card;

    private Goal item;
    private GoalCardViewHolder viewHolder;

    // for action recyclerview in the card
    private ArrayList<Action> actions;
    private DataComponentAdapter<Action> actionsAdapter;

    private ArrayList<SharedAction.Data> sharedActions;
    private DataComponentAdapter<SharedAction.Data> sharedActionsAdapter;

    private ComponentManager componentManager;

    private boolean isUserInvolved;
    private boolean isPersonal;

    public GoalCardComponent(Goal.GoalData bundle) {
        super();
        this.item = bundle.goal;
        this.isUserInvolved = bundle.userIsInvolved;
        this.isPersonal = false; // FIXME: item.getIsPersonal();
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new GoalCardViewHolder(view);
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
        return "GoalCardComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        item.fetchIfNeededInBackground((goal, e) -> {
            if (e != null) {
                Log.e("GoalCardComponent", "Failed to load goal", e);
                Toast.makeText(getActivity(), "Failed to load goal", Toast.LENGTH_LONG).show();
                return;
            }

        checkViewHolderClass(holder, GoalCardViewHolder.class);
        viewHolder = (GoalCardViewHolder) holder;

            Goal item = (Goal) goal;
        viewHolder.clLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalDetailsActivity.class);
                intent.putExtra("goal", item);

                    // fixme -- add ability to alter priority of goals as go back to goals fragment

                getActivity().startActivity(intent);
            }
        });

        viewHolder.tvName.setText(item.getTitle());
        viewHolder.tvDescription.setText(item.getDescription());

        GoalUtils.getNumActionsComplete(item, User.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            viewHolder.goalProgressBar.setProgress(progress);
        });

            viewHolder.tvQuote.setVisibility(View.GONE); // fixme -- to include quotes
            viewHolder.tvNumDone.setVisibility(View.GONE); // fixme -- can add personal bool, alter accordingly
            viewHolder.tvTag.setVisibility(View.GONE); // todo -- include tag functionality

        // get and render the actions -- use bool isPersonal and bool isUserInvolved

        // fixme -- jank casework
        if (isPersonal) {
            // a personal goal that only the creator can see, should always be the case

                actions = new ArrayList<>();

            actionsAdapter = new DataComponentAdapter<Action>(getActivity(), actions) {

                    @Override
                    protected Component makeComponent(Action item, ViewHolder holder) {
                        return new ActionComponent(item);
                    }

                    @Override
                    protected Component.Inflater makeInflater(Action item) {
                        return new ActionComponent.Inflater();
                    }
                };

                viewHolder.rvActions.setLayoutManager(new LinearLayoutManager(getActivity()));
                viewHolder.rvActions.setAdapter(actionsAdapter);

                GoalUtils.loadGoalActions(item, (objects) -> {
                    updateAdapter(objects, actions, actionsAdapter, viewHolder.rvActions);
                });
            }
            else if (!isPersonal && isUserInvolved) {
                // a social goal that the user is involved in BUT user is not author
                // for now, user sees official social goal

                // fixme -- for now, social goals can't be edited from the cards.
                // todo -- make it so creator can edit via goal details page ?

                sharedActions = new ArrayList<>();

                sharedActionsAdapter = new DataComponentAdapter<SharedAction.Data>(getActivity(), sharedActions) {
                    @Override
                    public Component makeComponent(SharedAction.Data item, Component.ViewHolder holder) {
                        Component component = new InvolvedSharedActionComponent(item);
                        return component;
                    }

                    @Override
                    protected Component.Inflater makeInflater(SharedAction.Data item) {
                        return new InvolvedSharedActionComponent.Inflater();
                    }
                };

                viewHolder.rvActions.setLayoutManager(new LinearLayoutManager(getActivity()));
                viewHolder.rvActions.setAdapter(sharedActionsAdapter);

                GoalUtils.loadGoalSharedActions(item, (objects) -> {
                    updateSharedAdapter(objects, sharedActions, sharedActionsAdapter, viewHolder.rvActions);
                });
            }
            else if (!isPersonal && !isUserInvolved) {
                // a social goal the user is not involved in

                sharedActions = new ArrayList<>();

                sharedActionsAdapter = new DataComponentAdapter<SharedAction.Data>(getActivity(), sharedActions) {
                    @Override
                    public Component makeComponent(SharedAction.Data item, ViewHolder holder) {
                        Component component = new UninvolvedSharedActionComponent(item);
                        return component;
                    }

                    @Override
                    protected Component.Inflater makeInflater(SharedAction.Data item) {
                        return new UninvolvedSharedActionComponent.Inflater();
                    }
                };

                viewHolder.rvActions.setLayoutManager(new LinearLayoutManager(getActivity()));
                viewHolder.rvActions.setAdapter(sharedActionsAdapter);

                GoalUtils.loadGoalSharedActions(item, (objects) -> {
                    updateSharedAdapter(objects, sharedActions, sharedActionsAdapter, viewHolder.rvActions);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {

    }

    private void updateSharedAdapter(List<SharedAction> objects, ArrayList<SharedAction.Data> sharedActions, DataComponentAdapter<SharedAction.Data> sharedActionsAdapter, RecyclerView rvActions) {
        // fixme -- similar to updateAdapter in GoalFragments; merge with that or the generic-typed updateAdapter?

        /* first need to find SharedAction.Data isUserDone boolean:
                1. for each sharedAction, query to find user's action in it
                2. check isDone and isConnectedToParentSharedAction boolean
                3. if (true, true) -> true. everything else false
          */

        AsyncUtils.waterfall(objects.size(), (Integer number, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // iteration in the for loop

            SharedAction sharedAction = objects.get(number);
            sharedAction.relChildActions.getQuery()
                    .whereEqualTo(KEY_PARENT_USER, getCurrentUser())
                    .findInBackground(new FindCallback<Action>() {
                        @Override
                        public void done(List<Action> objects, ParseException e) {
                            if (e == null && objects.size() == 1) {
                                Log.d(TAG, "found child action");

                                Action action = objects.get(0);
                                boolean isUserDone = (action.getIsDone() && action.getIsConnectedToParent());
                                SharedAction.Data data = new SharedAction.Data(sharedAction, isUserDone);
                                sharedActions.add(0, data);
                                sharedActionsAdapter.notifyItemInserted(0);

                            }
                            else {
                                Log.e(TAG, "either size(actions) wrong or error", e);
                            }
                            callback.call(e);
                        }
                    });
        }, (e) -> {
            rvActions.scrollToPosition(0);
        });
    }

    private < E > void updateAdapter(List< E > objects, ArrayList< E > actions, DataComponentAdapter< E > actionsAdapter, RecyclerView rvActions) {

        for (int i = 0; i < objects.size(); i++) {
            // load into recyclerview
            E action = objects.get(i);
            actions.add(0, action);
            actionsAdapter.notifyItemInserted(0);
        }

        rvActions.scrollToPosition(0);

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

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new GoalCardViewHolder(view);
        }
    }
}
