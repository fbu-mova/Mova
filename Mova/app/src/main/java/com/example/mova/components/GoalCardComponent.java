package com.example.mova.components;

import android.app.Activity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.utils.ColorUtils;
import com.example.mova.views.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.icons.Icons;
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

import static com.example.mova.views.GoalProgressBar.PROGRESS_MAX;
import static com.example.mova.model.Action.KEY_PARENT_USER;
import static com.example.mova.model.User.getCurrentUser;

public class GoalCardComponent extends Component {

    // fixme -- is there a diff between personal goal cards and social goal cards? social goals will be goals in a post?

    private static final String TAG = "goal card comp";
    private static final int viewLayoutRes = R.layout.item_goal_card;

    private Goal goal;
    public GoalCardViewHolder viewHolder;

    // for action recyclerview in the card
    private ArrayList<Action> actions;
    public static DataComponentAdapter<Action> actionsAdapter;

    private ArrayList<SharedAction.Data> sharedActions;
    private DataComponentAdapter<SharedAction.Data> sharedActionsAdapter;

    private ComponentManager componentManager;

    private boolean isUserInvolved;
    private boolean isPersonal;

    public GoalCardComponent(Goal.GoalData bundle) {
        super();
        this.goal = bundle.goal;
        this.isUserInvolved = bundle.userIsInvolved;
        this.isPersonal = goal.getIsPersonal();
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

        checkViewHolderClass(holder, GoalCardViewHolder.class);
        viewHolder = (GoalCardViewHolder) holder;

        viewHolder.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalDetailsActivity.class);
                intent.putExtra("goal", goal);
                intent.putExtra("isUserInvolved", isUserInvolved);

                    // fixme -- add ability to alter priority of goals as go back to goals fragment

                getActivity().startActivity(intent);
            }
        });

        viewHolder.tvName.setText(goal.getTitle());
//        viewHolder.tvDescription.setText(goal.getDescription());

        GoalUtils.getNumActionsComplete(goal, User.getCurrentUser(), (portionDone) -> {
            getActivity().runOnUiThread(() -> {
                int progress = (int) (portionDone * PROGRESS_MAX);
                viewHolder.goalProgressBar.setProgress(progress);
            });
        });

        Icons.from(getActivity()).displayNounIcon(goal, viewHolder.cvGoal, viewHolder.ivGoal);

        Resources res = getActivity().getResources();
        viewHolder.goalProgressBar.setUnfilledColor(ColorUtils.getColor(res, goal.getHue(), ColorUtils.Lightness.UltraLight));
        viewHolder.goalProgressBar.setFilledColor(ColorUtils.getColor(res, goal.getHue(), ColorUtils.Lightness.Mid));

        if(goal.getGroup() == null){
            viewHolder.tvFrom.setVisibility(View.GONE);
            viewHolder.tvGroup.setVisibility(View.GONE);
            viewHolder.cvFromGroupIcon.setVisibility(View.GONE);
        }else{
            goal.getGroupFull(() -> {}, (group) -> {
                getActivity().runOnUiThread(() -> {
                    viewHolder.tvGroup.setText(group.getName());
                    Icons.from(getActivity()).displayNounIcon(group, viewHolder.cvFromGroupIcon, viewHolder.ivFromGroupIcon);
                });
            });
        }

//            viewHolder.tvQuote.setVisibility(View.GONE); // fixme -- to include quotes
//            viewHolder.tvNumDone.setVisibility(View.GONE); // fixme -- can add personal bool, alter accordingly
//            viewHolder.tvTag.setVisibility(View.GONE); // todo -- include tag functionality

        // get and render the actions -- use bool isPersonal and bool isUserInvolved

        // fixme -- jank casework
        if (isPersonal) {
            // a personal goal that only the creator can see, should always be the case

            actions = new ArrayList<>();

            actionsAdapter = new DataComponentAdapter<Action>(getActivity(), actions) {

                    @Override
                    protected Component makeComponent(Action item, ViewHolder holder) {
                        return new ActionComponent(goal, item, GoalCardComponent.this.isPersonal);
                    }

                    @Override
                    protected Component.Inflater makeInflater(Action item) {
                        return new ActionComponent.Inflater();
                    }
                };

            viewHolder.rvActions.setLayoutManager(new LinearLayoutManager(getActivity()));
            viewHolder.rvActions.setAdapter(actionsAdapter);

            GoalUtils.loadGoalActions(goal, (objects) -> {
                getActivity().runOnUiThread(() -> {
                    updateAdapter(objects, actions, actionsAdapter, viewHolder.rvActions);
                });
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
                    Component component = new InvolvedSharedActionComponent(item, goal.getHue());
                    return component;
                }

                @Override
                protected Component.Inflater makeInflater(SharedAction.Data item) {
                    return new InvolvedSharedActionComponent.Inflater();
                }
            };

            viewHolder.rvActions.setLayoutManager(new LinearLayoutManager(getActivity()));
            viewHolder.rvActions.setAdapter(sharedActionsAdapter);

            GoalUtils.loadGoalSharedActions(goal, (objects) -> {
                getActivity().runOnUiThread(() -> {
                    updateInvolvedSharedAdapter(getActivity(), objects, sharedActions, sharedActionsAdapter, viewHolder.rvActions);
                });
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

            GoalUtils.loadGoalSharedActions(goal, (objects) -> {
                getActivity().runOnUiThread(() -> {
                    updateUninvolvedSharedAdapter(getActivity(), goal, objects, sharedActions, sharedActionsAdapter, viewHolder.rvActions);
                });
            });
        }
    }

    @Override
    protected void onDestroy() {

    }

    public static void updateInvolvedSharedAdapter(Activity activity, List<SharedAction> objects, ArrayList<SharedAction.Data> sharedActions, DataComponentAdapter<SharedAction.Data> sharedActionsAdapter, RecyclerView rvActions) {
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
                        public void done(List<Action> objectsList, ParseException e) {
                            activity.runOnUiThread(() -> {
                                if (e == null && objects.size() == 1) {
                                    Log.d(TAG, "found child action");

                                Action action = objectsList.get(0);
                                boolean isUserDone = (action.getIsDone() && action.getIsConnectedToParent());
                                SharedAction.Data data = new SharedAction.Data(sharedAction, isUserDone);
                                sharedActions.add(0, data);
                            }
                            else {
                                Log.e(TAG, "either size(actions) wrong or error", e);
                            }
                            callback.call(e);
                            });
                        }
                    });
        }, (e) -> {
            activity.runOnUiThread(() -> {
            sharedActionsAdapter.notifyItemRangeInserted(0, objects.size());
            rvActions.scrollToPosition(0);
            });
        });
    }

    public static void updateUninvolvedSharedAdapter(Activity activity, Goal goal, List<SharedAction> objects, ArrayList<SharedAction.Data> sharedActions, DataComponentAdapter<SharedAction.Data> sharedActionsAdapter, RecyclerView rvActions) {
        // don't need to check user completion / connection. only need to display order of SharedActions given priority
        activity.runOnUiThread(() -> {
            GoalUtils.loadGoalSharedActions(goal, (sharedActionsList) -> {

                for (SharedAction sharedAction : sharedActionsList) {
                    sharedActions.add(0, new SharedAction.Data(sharedAction, false));
                }
            sharedActionsAdapter.notifyItemRangeInserted(0, sharedActionsList.size());
                rvActions.scrollToPosition(0);
            });
        });
    }

    private < E > void updateAdapter(List< E > objects, ArrayList< E > actions, DataComponentAdapter< E > actionsAdapter, RecyclerView rvActions) {
        for (int i = 0; i < objects.size(); i++) {
            // load into recyclerview
            E action = objects.get(i);
            actions.add(0, action);
        }
        actionsAdapter.notifyItemRangeInserted(0, objects.size());
        rvActions.scrollToPosition(0);
    }

    public static class GoalCardViewHolder extends Component.ViewHolder {

        @BindView(R.id.goalProgressBar) protected GoalProgressBar goalProgressBar;
//        @BindView(R.id.tvQuote)         protected TextView tvQuote; // todo -- add to Parse ? stretch goal
        @BindView(R.id.tvName)          protected TextView tvName;
//        @BindView(R.id.tvDescription)   protected TextView tvDescription;
        @BindView(R.id.rvActions)       protected RecyclerView rvActions;
//        @BindView(R.id.tvNumDone)       protected TextView tvNumDone; // fixme -- in personal, only one person ?
//        @BindView(R.id.tvTag)           protected TextView tvTag; // fixme -- what about multiple tags?
        @BindView(R.id.layout)          protected LinearLayout llLayout;
        @BindView(R.id.cvFromGroupIcon) protected CardView cvFromGroupIcon;
        @BindView(R.id.ivFromGroupIcon) protected ImageView ivFromGroupIcon;
        @BindView(R.id.cvGroup)         protected CardView cvGoal;
        @BindView(R.id.ivGroup)         protected ImageView ivGoal;
        @BindView(R.id.tvFrom) protected TextView tvFrom;
        @BindView(R.id.tvGroup) TextView tvGroup;

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
