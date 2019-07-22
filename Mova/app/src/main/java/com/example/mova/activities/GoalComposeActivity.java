package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mova.R;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.activities.JournalComposeActivity.KEY_COMPOSED_POST_TAGS;

public class GoalComposeActivity extends AppCompatActivity {

    private static final String TAG = "Goal Compose Activity";
    private static final String KEY_COMPOSED_GOAL = "composed goal";

    // todo : currently most basic (and only personal). features to add:
        // can add 'infinite' number of actions (dynamically create editTexts?)
        // add image (plus resizing) at some point
        // add tags
        // add icons for actions

    @BindView(R.id.etGoalName)              protected EditText etGoalName;
    @BindView(R.id.etGoalDescription)       protected EditText etGoalDescription;
    @BindView(R.id.etAction)                protected EditText etAction;
    @BindView(R.id.btSubmit)                protected Button btSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_compose);
        ButterKnife.bind(this);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalName = etGoalName.getText().toString();
                String goalDescription = etGoalDescription.getText().toString();
                String action = etAction.getText().toString();

                submitPersonalGoal(goalName, goalDescription, action);
            }
        });
    }

    private void submitPersonalGoal(String goalName, String goalDescription, String task) {
        // todo -- include image choosing for goal image
        // fixme -- update to also encompass Social functionality ?

        Goal goal = new Goal()
                .setAuthor((User) ParseUser.getCurrentUser())
                .setTitle(goalName)
                .setDescription(goalDescription);

        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Initial goal save successful");

                    // save the SharedAction
                    SharedAction sharedAction = new SharedAction()
                            .setTask(task)
                            .setGoal(goal)
                            .setUsersDone(0);

                    AsyncUtils.saveWithRelation(sharedAction, goal.relSharedActions, new AsyncUtils.ItemCallback() {
                        @Override
                        public void call(Object item) { // sharedAction is item
                            Log.d(TAG, "Saving SharedAction successful");

                            // save the Action
                            Action action = new Action()
                                    .setTask(task)
                                    .setParentGoal(goal)
                                    .setParentUser((User) ParseUser.getCurrentUser())
                                    .setParentSharedAction(sharedAction);

                            AsyncUtils.saveWithRelation(action, sharedAction.relChildActions, new AsyncUtils.ItemCallback() {
                                @Override
                                public void call(Object item) { // action is item
                                    Log.d(TAG, "Saving Action successful");

                                    // everything (for now) is saved, want to go back to Goal feed and update recyclerview

                                    getIntent().putExtra(KEY_COMPOSED_GOAL, goal);
                                    // getIntent().putExtra(KEY_COMPOSED_POST_TAGS, tagObjects);
                                    setResult(RESULT_OK, getIntent());
                                    finish();
                                }
                            });
                        }
                    });

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
}
