package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mova.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalComposeActivity extends AppCompatActivity {

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

                submitGoal(goalName, goalDescription, action);
            }
        });
    }

    private void submitGoal(String goalName, String goalDescription, String action) {
        // save goal + action onto Parse database
        
    }
}
