package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.adapters.ComposeActionsAdapter;
import com.example.mova.component.ComponentLayout;
import com.example.mova.components.ActionComponent;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.SharedAction;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalComposeActivity extends DelegatedResultActivity {

    private static final String TAG = "Goal Compose Activity";
    public static final String KEY_COMPOSED_GOAL = "composed goal";
    public static final int REQUEST_GOAL_DETAILS = 19;

    // todo : currently most basic (and only personal). features to add:
    // add image (plus resizing) at some point
    // add tags
    // add icons for actions

    @BindView(R.id.etGoalName)          protected EditText etGoalName;
    @BindView(R.id.etGoalDescription)   protected EditText etGoalDescription;
    //    @BindView(R.id.etAction)                protected EditText etAction;
    @BindView(R.id.btSubmit)            protected Button btSubmit;
    @BindView(R.id.rvComposeAction)     protected RecyclerView rvComposeAction;
//    @BindView(R.id.etAddAction)         protected EditText etAddAction;
// FIXME: save button of action currently assumes goal already saved
    @BindView(R.id.clAddAction)         protected ComponentLayout clAddAction;

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

                GoalUtils.submitGoal(goalName, goalDescription, actions, true, (goal) -> endActivity(goal));
            }
        });

        clAddAction.inflateComponent(GoalComposeActivity.this, new ActionComponent(new Action().setTask("Add task here")));

//        // add 'enter' soft keyboard usage for etAddAction
//        etAddAction.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//
//                    // save added action to the adapter, clear etAddAction
//                    String newAction = etAddAction.getText().toString();
//                    actions.add(newAction); // put at end rather than beginning
//                    actionAdapter.notifyItemInserted(actions.size() - 1);
//                    etAddAction.setText("");
//
//                    handled = true;
//                }
//                return handled;
//            }
//        });
    }

    private void endActivity(Goal goal) {
        // go to general goal fragment page (or details page?)
        getIntent().putExtra(KEY_COMPOSED_GOAL, goal);
        // getIntent().putExtra(KEY_COMPOSED_POST_TAGS, tagObjects);
        setResult(RESULT_OK, getIntent());
        finish();
    }
}
