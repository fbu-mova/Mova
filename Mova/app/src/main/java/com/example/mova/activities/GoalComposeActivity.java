package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.adapters.ComposeActionsAdapter;
import com.example.mova.fragments.Personal.ComposeActionDialog;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalComposeActivity extends AppCompatActivity {

    private static final String TAG = "Goal Compose Activity";
    public static final String KEY_COMPOSED_GOAL = "composed goal";
    public static final int REQUEST_GOAL_DETAILS = 19;

    // todo : currently most basic (and only personal). features to add:
        // can add 'infinite' number of actions (dynamically create editTexts?)
            // using modal overlay/dialog fragment
        // add image (plus resizing) at some point
        // add tags
        // add icons for actions

    @BindView(R.id.etGoalName)              protected EditText etGoalName;
    @BindView(R.id.etGoalDescription)       protected EditText etGoalDescription;
//    @BindView(R.id.etAction)                protected EditText etAction;
    @BindView(R.id.btSubmit)                protected Button btSubmit;
    @BindView(R.id.rvComposeAction)         protected RecyclerView rvComposeAction;
    @BindView(R.id.etAddAction)             protected EditText etAddAction;

    ComposeActionsAdapter actionAdapter;
    List<String> actions;

    List<SharedAction> sharedActionsList;
    List<Action> actionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_compose); // fixme: layout is jank when adding many actions
        ButterKnife.bind(this);

        actions = new ArrayList<>();
        actionAdapter = new ComposeActionsAdapter(actions);
        rvComposeAction.setLayoutManager(new LinearLayoutManager(this));
        rvComposeAction.setAdapter(actionAdapter);

        sharedActionsList = new ArrayList<>();
        actionsList = new ArrayList<>();

        btSubmit.setOnClickListener(new View.OnClickListener() { // todo -- maybe make bottom nav to help w/ jank layout ?
            @Override
            public void onClick(View v) {
                String goalName = etGoalName.getText().toString();
                String goalDescription = etGoalDescription.getText().toString();

                submitPersonalGoal(goalName, goalDescription, actions);
            }
        });

        // add 'enter' soft keyboard usage for etAddAction
        etAddAction.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    // save added action to the adapter, clear etAddAction
                    String newAction = etAddAction.getText().toString();
                    actions.add(newAction); // put at end rather than beginning
                    actionAdapter.notifyItemInserted(actions.size() - 1);
                    etAddAction.setText("");

                    handled = true;
                }
                return handled;
            }
        });
    }

    private void submitPersonalGoal(String goalName, String goalDescription, List<String> actions) {

        // currently todo-ing: saving multiple actions within a goal (include shareAction process)

        // todo -- include image choosing for goal image + color
        // todo -- update to also encompass Social functionality ?

        Goal goal = new Goal()
                .setAuthor((User) ParseUser.getCurrentUser())
                .setTitle(goalName)
                .setDescription(goalDescription);

        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Initial goal save successful");

                    saveSharedAction(actions, goal);
                }
                else {
                    Log.e(TAG, "Initial goal save unsuccessful", e);
                }
            }
        });
    }

    private void saveSharedAction(List<String> actions, Goal goal) {

        // create a SharedAction for each action in actions

        AsyncUtils.executeMany(actions.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // the iteration in the for loop

            SharedAction sharedAction = new SharedAction()
                    .setTask(actions.get(item))
                    .setGoal(goal)
                    .setUsersDone(0);

            sharedActionsList.add(sharedAction);

            // save sharedAction
            sharedAction.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Saved SharedAction successfully");
                    } else {
                        Log.e(TAG, "SharedAction failed saving", e);
                    }
                    callback.call(e);
                }
            });
        }, (e) -> {
            // when whole for-loop has run though -- save goal
            Log.d(TAG, "in saveSharedAction final callback");

            // go to create and save actions
            saveAction(actions, goal, sharedActionsList);
        });

    }

    private void saveAction(List<String> actions, Goal goal, List<SharedAction> sharedActionsList) {

        AsyncUtils.executeMany(actions.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // the iteration in the for loop

            // save the Action
            Action action = new Action()
                    .setTask(actions.get(item))
                    .setParentGoal(goal)
                    .setParentUser((User) ParseUser.getCurrentUser())
                    .setParentSharedAction(sharedActionsList.get(item));

            actionsList.add(action);

            // save action
            action.saveInBackground((ParseException e) -> {
                if (e == null) {
                    Log.d(TAG, "Saved Action successfully");

                    // add to specific sharedAction's relation
                    sharedActionsList.get(item).relChildActions.add(action);
                    sharedActionsList.get(item).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "sharedAction added child action rel");
                            }
                            else {
                                Log.e(TAG, "sharedAction failed to add child action rel", e);
                            }
                            callback.call(e);
                        }
                    });
                }
                else {
                    Log.e(TAG, "Action failed saving", e);
                    callback.call(e);
                }
            });
        }, (e) -> {
            // when whole for-loop has run though -- save goal
            Log.d(TAG, "in saveAction final callback");

            updateGoalRels(sharedActionsList, actionsList, goal);
        });
    }

    private void updateGoalRels(List<SharedAction> sharedActionsList, List<Action> actionsList, Goal goal) {

        AsyncUtils.executeMany(actions.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
            // the iteration in the for loop

            goal.relSharedActions.add(sharedActionsList.get(item)); // fixme (?) need an execute many callback ?
            goal.relActions.add(actionsList.get(item)); // has same length as sharedActionsList
            callback.call(null);
        }, (e) -> {

            goal.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "finished final goal update");

                        // go to general goal fragment page (or details page?)
                        getIntent().putExtra(KEY_COMPOSED_GOAL, goal);
                        // getIntent().putExtra(KEY_COMPOSED_POST_TAGS, tagObjects);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                    else {
                        Log.e(TAG, "failed to finish final goal update", e);
                    }
                }
            });
        });
    }
}
