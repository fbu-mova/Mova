package com.example.mova.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.components.ActionComponent;
import com.example.mova.components.CreateActionComponent;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.InvolvedSharedActionComponent;
import com.example.mova.components.UninvolvedSharedActionComponent;
import com.example.mova.dialogs.ConfirmShareGoalDialog;
import com.example.mova.icons.Icons;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.GoalProgressBar.PROGRESS_MAX;
import static com.example.mova.model.Action.KEY_CREATED_AT;
import static com.example.mova.model.Action.KEY_PARENT_USER;

public class GoalDetailsActivity extends DelegatedResultActivity {

    private static final String TAG = "goal details activity";
    private Goal goal;

    User user;
    private boolean isPersonal;
    private boolean isUserInvolved;

    @BindView(R.id.ivIcon)         protected ImageView ivPhoto;
    @BindView(R.id.tvName)          protected TextView tvGoalName;
    @BindView(R.id.tvGroupName)     protected TextView tvGroupName;
    @BindView(R.id.tvDescription)   protected TextView tvDescription;
    @BindView(R.id.rvActions)       protected RecyclerView rvActions;
    @BindView(R.id.goalpb)          protected GoalProgressBar goalpb;
    @BindView(R.id.ivShare)         protected ImageView ivShare;
    @BindView(R.id.ivSave)          protected ImageView ivSave;
    @BindView(R.id.clAddAction)     protected ComponentLayout clAddAction;
    @BindView(R.id.cvIcon)          protected CardView cvIcon;


    @BindView(R.id.llGroupDetails)  protected LinearLayout llGroupDetails;
    @BindView(R.id.llShareGoal)     protected LinearLayout llShareGoal;
    @BindView(R.id.llSaveGoal)      protected LinearLayout llSaveGoal;

    // recyclerview - case personal
    private List<Action> actions;
    private DataComponentAdapter<Action> actionsAdapter;

    // recyclerview - case social
    private ArrayList<SharedAction.Data> sharedActions;
    private DataComponentAdapter<SharedAction.Data> sharedActionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);
        ButterKnife.bind(this);

        user = User.getCurrentUser();
        goal = getIntent().getParcelableExtra("goal");
        isUserInvolved = getIntent().getBooleanExtra("isUserInvolved", false);

        isPersonal = goal.getIsPersonal();
        tvGoalName.setText(goal.getTitle());

        Icons.from(GoalDetailsActivity.this).displayNounIcon(goal, cvIcon, ivPhoto);

        goal.getGroupName(() -> {
            tvGroupName.setVisibility(View.GONE);
            displayHeader(false);
        }, (str) -> {
            if (str == "") {
                tvGroupName.setVisibility(View.GONE);
                displayHeader(false);
            }
            else {
                tvGroupName.setText(str);
                displayHeader(true);
            }

        });

        tvDescription.setText(goal.getDescription());

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmShare();
            }
        });

        ivSave.setOnClickListener((v) -> {
            if (isPersonal) {
                // fixme -- what if not in same group as this goal? can still see in first place? ( ~this case)
                Toast.makeText(GoalDetailsActivity.this, "You can't save someone else's personal goal!", Toast.LENGTH_LONG).show();
            }
            else {
                // save social goal as a personal goal
                GoalUtils.saveSocialGoal(goal, this);
            }
        });

//        String url = (goal.getImage() != null) ? goal.getImage().getUrl() : "";
//        Glide.with(this)  // fixme -- always take forever to load
//                .load(url)
//                .error(R.color.colorPrimaryDark)
//                .placeholder(R.color.orangeMid)
//                .into(ivPhoto);

        // update GoalProgressBar
        GoalUtils.getNumActionsComplete(goal, User.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            goalpb.setProgress(progress);
        });

        if (isPersonal || user.equals(goal.getAuthor())) { // FIXME -- currently social goals can't add actions (getting author User object also a callback itself...)
            inflateAddActionComponent();
        }

        setUpRecyclerView();

    }

    private void displayHeader(boolean hasGroup) {
        // decides which of the headers to display

        if (isPersonal) {
            llGroupDetails.setVisibility(View.GONE);
            llSaveGoal.setVisibility(View.GONE);
            llShareGoal.setVisibility(View.VISIBLE);
        }
        else if (hasGroup) {
            llGroupDetails.setVisibility(View.VISIBLE);
            llShareGoal.setVisibility(View.GONE);
            if (isUserInvolved) llSaveGoal.setVisibility(View.GONE);
            else                llSaveGoal.setVisibility(View.VISIBLE);
        }
        else if (!hasGroup) {
            llGroupDetails.setVisibility(View.GONE);
            llShareGoal.setVisibility(View.VISIBLE);
            if (isUserInvolved) llSaveGoal.setVisibility(View.GONE);
            else                llSaveGoal.setVisibility(View.VISIBLE);


        }
    }

    private void inflateAddActionComponent() {

        clAddAction.inflateComponent(GoalDetailsActivity.this, new CreateActionComponent(new GoalComposeActivity.HandleCreateAction() {
            @Override
            public void call(Action action) {
                SharedAction sharedAction = new SharedAction()
                        .setTask(action.getTask())
                        .setIsPriority(action.getIsPriority())
                        .setUsersDone(0)
                        .setGoal(goal);

                // save sharedAction
                sharedAction.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            sharedAction.relChildActions.add(action);
                            sharedAction.relUsersInvolved.add(User.getCurrentUser());

                            // save sharedAction again
                            sharedAction.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // action needs to save parentGoal and parentSharedAction
                                        action.setParentGoal(goal);
                                        action.setParentSharedAction(sharedAction);
                                        action.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {

                                                    // need to update and save goal
                                                    goal.relSharedActions.add(sharedAction);
                                                    goal.relActions.add(action);

                                                    goal.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Log.d(TAG, "create action success");

                                                                // add action (or sharedAction, depending on casework...) to recyclerview
                                                                updateRecyclerView(action, sharedAction);
                                                            }
                                                            else {
                                                                Log.e(TAG, "create action failed");
                                                            }
                                                        }
                                                    });
                                                }
                                                else {
                                                    Log.e(TAG, "penultimate create action failed", e);
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        Log.e(TAG, "create action save 2 of shared action failed", e);
                                    }
                                }
                            });
                        }
                        else {
                            Log.e(TAG, "create action save 1 of shared action failed", e);
                        }
                    }
                });
            }
        }));

    }

    private void updateRecyclerView(Action action, SharedAction sharedAction) {

        if (isPersonal) {
            actions.add(action);
            actionsAdapter.notifyItemInserted(actions.size() - 1);
        }
        else if (!isPersonal) { // here, can only add if creator of social goal
            sharedActions.add(new SharedAction.Data(sharedAction, false));
            sharedActionsAdapter.notifyItemInserted(sharedActions.size() - 1);
        }

    }

    private void setUpRecyclerView() {

        // recyclerview -- casework like in GoalCardComp
        if (isPersonal) {
            actions = new ArrayList<>();

            actionsAdapter = new DataComponentAdapter<Action>(this, actions) {
                @Override
                public Component makeComponent(Action item, Component.ViewHolder holder) {
                    Component component = new ActionComponent(item, isPersonal);
                    return component;
                }

                @Override
                protected Component.Inflater makeInflater(Action item) {
                    return new ActionComponent.Inflater();
                }
            };

            rvActions.setLayoutManager(new LinearLayoutManager(this));
            rvActions.setAdapter(actionsAdapter);

            loadAllActions(); // fixme : mentioned in method declaration, but needs to address casework
        }
        else if (!isPersonal && isUserInvolved) {
            // user sees official social goal

            // a social goal that the user is involved in BUT user is not author
            // for now, user sees official social goal

            // fixme -- for now, social goals can't be edited from the cards.
            // todo -- make it so creator can edit via goal details page ?

            sharedActions = new ArrayList<>();

            sharedActionsAdapter = new DataComponentAdapter<SharedAction.Data>(this, sharedActions) {
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

            rvActions.setLayoutManager(new LinearLayoutManager(this));
            rvActions.setAdapter(sharedActionsAdapter);

            GoalUtils.loadGoalSharedActions(goal, (objects) -> {
                GoalCardComponent.updateInvolvedSharedAdapter(objects, sharedActions, sharedActionsAdapter, rvActions);
            });
        }
        else if (!isPersonal && !isUserInvolved) {
            // user doesn't have checkbox functionality

            // a social goal the user is not involved in

            sharedActions = new ArrayList<>();

            sharedActionsAdapter = new DataComponentAdapter<SharedAction.Data>(this, sharedActions) {
                @Override
                public Component makeComponent(SharedAction.Data item, Component.ViewHolder holder) {
                    Component component = new UninvolvedSharedActionComponent(item);
                    return component;
                }

                @Override
                protected Component.Inflater makeInflater(SharedAction.Data item) {
                    return new UninvolvedSharedActionComponent.Inflater();
                }
            };

            rvActions.setLayoutManager(new LinearLayoutManager(this));
            rvActions.setAdapter(sharedActionsAdapter);

            GoalUtils.loadGoalSharedActions(goal, (objects) -> {
                GoalCardComponent.updateUninvolvedSharedAdapter(goal, objects, sharedActions, sharedActionsAdapter, rvActions);
            });
        }

    }

    private void confirmShare() {

        FragmentManager fm = getSupportFragmentManager();
        ConfirmShareGoalDialog confirmShareGoalDialog = ConfirmShareGoalDialog.newInstance(goal);
        confirmShareGoalDialog.show(fm, "showingConfirmShareGoalDialog");

    }

    private void loadAllActions() { // fixme : should be same as GoalCardComp, visible even if not involved

        // query for all the actions of this goal that is from the user

        ParseQuery<Action> query = goal.relActions.getQuery()
                .whereEqualTo(KEY_PARENT_USER, User.getCurrentUser())
                .orderByDescending(KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Action>() {
            @Override
            public void done(List<Action> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    Action action = objects.get(i);
                    actions.add(0, action);
                    actionsAdapter.notifyItemInserted(0);
                }
                rvActions.scrollToPosition(0);
            }
        });
    }
}
