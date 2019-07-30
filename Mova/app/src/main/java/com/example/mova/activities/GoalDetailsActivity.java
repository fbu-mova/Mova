package com.example.mova.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.ConfirmShareGoalDialog;
import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.ActionComponent;
import com.example.mova.components.Component;
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

import static com.example.mova.GoalProgressBar.PROGRESS_MAX;
import static com.example.mova.model.Action.KEY_PARENT_USER;

public class GoalDetailsActivity extends DelegatedResultActivity {

    private static final String TAG = "goal details activity";
    private Goal goal;

    private boolean isPersonal;

    @BindView(R.id.ivPhoto)         protected ImageView ivPhoto;
    @BindView(R.id.tvName)          protected TextView tvGoalName;
    @BindView(R.id.tvFromGroup)     protected TextView tvFromGroup;
    @BindView(R.id.tvDescription)   protected TextView tvDescription;
    @BindView(R.id.rvActions)       protected RecyclerView rvActions;
    @BindView(R.id.goalpb)          protected GoalProgressBar goalpb;
    @BindView(R.id.ivShare)         protected ImageView ivShare;
    @BindView(R.id.ivSave)          protected ImageView ivSave;

    // recyclerview
    private List<Action> actions;
    private DataComponentAdapter<Action> actionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_details);
        ButterKnife.bind(this);

        goal = getIntent().getParcelableExtra("goal");

        tvGoalName.setText(goal.getTitle());
        tvFromGroup.setText(goal.getGroupName());
        tvDescription.setText(goal.getDescription());

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmShare();
            }
        });

        ivSave.setOnClickListener((v) -> {
            if (goal.getIsPersonal()) {
                // fixme -- what if not in same group as this goal? can still see in first place? ( ~this case)
                Toast.makeText(GoalDetailsActivity.this, "You can't save someone else's personal goal!", Toast.LENGTH_LONG).show();
            }
            else {
                // save social goal as a personal goal
                GoalUtils.saveSocialGoal(goal, (User) ParseUser.getCurrentUser());
            }
        });

        String url = (goal.getImage() != null) ? goal.getImage().getUrl() : "";

        Glide.with(this)
                .load(url)
                .error(R.color.colorPrimaryDark)
                .into(ivPhoto);

        // update GoalProgressBar
        GoalUtils.getNumActionsComplete(goal, (User) ParseUser.getCurrentUser(), (portionDone) -> {
            int progress = (int) (portionDone * PROGRESS_MAX);
            goalpb.setProgress(progress);
        });

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

    private void confirmShare() {

        FragmentManager fm = getSupportFragmentManager();
        ConfirmShareGoalDialog confirmShareGoalDialog = ConfirmShareGoalDialog.newInstance(goal);
        confirmShareGoalDialog.show(fm, "showingConfirmShareGoalDialog");

    }

    // FIXME -- going back + refresh not happening issues
    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent intent = new Intent(this, getCallingActivity().getClass());
//        intent.putExtra();
//        setResult(RESULT_OK, intent);
//        finish();
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
