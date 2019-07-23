package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalComposeActivity extends AppCompatActivity implements ComposeActionDialog.SubmitActionDialogListener {

    private static final String TAG = "Goal Compose Activity";
    public static final String KEY_COMPOSED_GOAL = "composed goal";

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

    ComposeActionsAdapter actionAdapter;
    List<String> actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_compose);
        ButterKnife.bind(this);

        actions = new ArrayList<>();
        actionAdapter = new ComposeActionsAdapter(actions);
        rvComposeAction.setLayoutManager(new LinearLayoutManager(this));
        rvComposeAction.setAdapter(actionAdapter);

        rvComposeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalName = etGoalName.getText().toString();
                String goalDescription = etGoalDescription.getText().toString();
//                String action = etAction.getText().toString();

//                submitPersonalGoal(goalName, goalDescription, action);
            }
        });
    }

    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeActionDialog dialog = new ComposeActionDialog();
        dialog.show(fm, "fragment_alert");

    }

    @Override
    public void onFinishActionDialog(String inputText) {
        // update recyclerview
        actions.add(0, inputText);
        actionAdapter.notifyItemInserted(0);
    }

    private void submitPersonalGoal(String goalName, String goalDescription, String task) {
        // todo -- include image choosing for goal image
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

                    saveSharedAction(task, goal);
                }
                else {
                    Log.e(TAG, "Initial goal save unsuccessful", e);
                }
            }
        });

        // put information into it, save in background

        // async outlining :

        // Goal to relation of sharedActions AND relation of actions
        // sharedAction to relation of child actions
        // actions point to parent sharedAction and parent goal

        // easier use case :

        // saving a journal entry --
        // save to Post database since it is a Post
        // save to User's Journal<Post> relation to access

    }

    private void saveSharedAction(String task, Goal goal) {

        // save the SharedAction
        SharedAction sharedAction = new SharedAction()
                .setTask(task)
                .setGoal(goal)
                .setUsersDone(0);

        AsyncUtils.saveWithRelation(sharedAction, goal.relSharedActions, new AsyncUtils.ItemCallback() {
            @Override
            public void call(Object item) { // sharedAction is item
                Log.d(TAG, "Saving SharedAction successful");

                saveAction(task, goal, sharedAction);
            }
        });
    }

    private void saveAction(String task, Goal goal, SharedAction sharedAction) {

        // save the Action
        Action action = new Action()
                .setTask(task)
                .setParentGoal(goal)
                .setParentUser((User) ParseUser.getCurrentUser())
                .setParentSharedAction(sharedAction);

        AsyncUtils.saveWithRelation(action, sharedAction.relChildActions, new AsyncUtils.ItemCallback() {
            @Override
            public void call(Object item) { // action is item
                Log.d(TAG, "Saving Action to SharedAction successful");
            }
        });

        AsyncUtils.saveWithRelation(action, goal.relActions, new AsyncUtils.ItemCallback() {
            @Override
            public void call(Object item) {
                // everything (for now) is saved, want to go back to Goal feed and update recyclerview
                // fixme -- with multiple actions, only can call this after all of the actions have been saved

                Log.d(TAG, "Saving Action to Goal successful");
                Toast.makeText(GoalComposeActivity.this, "Goal saved successfully!", Toast.LENGTH_LONG).show();

                getIntent().putExtra(KEY_COMPOSED_GOAL, goal);
                // getIntent().putExtra(KEY_COMPOSED_POST_TAGS, tagObjects);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
    }

}
