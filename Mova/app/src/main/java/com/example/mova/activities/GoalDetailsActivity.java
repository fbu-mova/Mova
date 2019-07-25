package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.ActionComponent;
import com.example.mova.components.ActionEditComponent;
import com.example.mova.components.ActionViewComponent;
import com.example.mova.components.Component;
import com.example.mova.fragments.Personal.GoalsFragment;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.model.Action.KEY_PARENT_USER;

public class GoalDetailsActivity extends DelegatedResultActivity {

    private Goal goal;
    public boolean edited;

    // so so so jank
    private Class previousClass;

    @BindView(R.id.ivPhoto)         protected ImageView ivPhoto;
    @BindView(R.id.tvName)          protected TextView tvGoalName;
    @BindView(R.id.tvFromGroup)     protected TextView tvFromGroup;
    @BindView(R.id.tvDescription)   protected TextView tvDescription;
    @BindView(R.id.rvActions)       protected RecyclerView rvActions;

    // recyclerview
    private List<Action> actions;
    private DataComponentAdapter<Action> actionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);
        ButterKnife.bind(this);

        goal = getIntent().getParcelableExtra("goal");

        previousClass = (getIntent().getStringExtra("previous activity") == "goal card component") ? MainActivity.class : null;

        tvGoalName.setText(goal.getTitle());
        tvFromGroup.setText(goal.getGroupName());
        tvDescription.setText(goal.getDescription());

        String url = (goal.getImage() != null) ? goal.getImage().getUrl() : "";

        Glide.with(this)
                .load(url)
                .error(R.color.colorPrimaryDark)
                .into(ivPhoto);

        // recyclerview
        actions = new ArrayList<>();

        actionsAdapter = new DataComponentAdapter<Action>(this, actions) {
            @Override
            public Component makeComponent(Action item) {

                Component component = new ActionComponent(item);
                return component;
            }
        };

        rvActions.setLayoutManager(new LinearLayoutManager(this));
        rvActions.setAdapter(actionsAdapter);

        loadAllActions();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("edited", edited);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void loadAllActions() {

        // query for all the actions of this goal that is from the user

        ParseQuery<Action> query = goal.relActions.getQuery().whereEqualTo(KEY_PARENT_USER, ParseUser.getCurrentUser());

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
