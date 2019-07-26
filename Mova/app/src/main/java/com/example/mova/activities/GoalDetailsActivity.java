package com.example.mova.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mova.ConfirmShareGoalDialog;
import com.example.mova.GoalProgressBar;
import com.example.mova.R;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.ActionComponent;
import com.example.mova.components.ActionEditComponent;
import com.example.mova.components.ActionViewComponent;
import com.example.mova.components.Component;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    @BindView(R.id.btShare)         protected Button btShare;

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

        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // todo -- create pop-up to confirm
                    // ask which group to share it in (or just friends)
                    // ask if want to include a description (need to create the post as well, with embedded media)
                    // include top toolbar in pop-up to confirm

                confirmShare();
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
