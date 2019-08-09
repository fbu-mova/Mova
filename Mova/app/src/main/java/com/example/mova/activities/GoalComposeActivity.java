package com.example.mova.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.adapters.ComposeActionsAdapter;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentLayout;
import com.example.mova.components.CreateActionComponent;
import com.example.mova.components.ImageComponent;
import com.example.mova.icons.Icons;
import com.example.mova.icons.NounProjectClient;
import com.example.mova.model.Action;
import com.example.mova.model.Goal;
import com.example.mova.utils.GoalUtils;

import java.util.ArrayList;
import java.util.Collections;
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
    @BindView(R.id.clAddAction)         protected ComponentLayout clAddAction;
    @BindView(R.id.cvIcon)              protected CardView cvIcon;
    @BindView(R.id.ivIcon)              protected ImageView ivIcon;

    ComposeActionsAdapter actionAdapter;
    List<String> actions;
    Goal goal;

    // TODO : current updates

    List<Action> unsavedActions; // unsaved because they're not saved to the goal yet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_compose); // fixme: layout is jank when adding many actions
        ButterKnife.bind(this);

        goal = new Goal();
        actions = new ArrayList<>();
        unsavedActions = new ArrayList<>();
        actionAdapter = new ComposeActionsAdapter(unsavedActions);
        rvComposeAction.setLayoutManager(new LinearLayoutManager(this));
        rvComposeAction.setAdapter(actionAdapter);

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalName = etGoalName.getText().toString();
                String goalDescription = etGoalDescription.getText().toString();

                GoalUtils.submitGoal(goal ,goalName, goalDescription, unsavedActions, true, (item) -> {

                    endActivity(item);});
            }
        });

        clAddAction.inflateComponent(GoalComposeActivity.this, new CreateActionComponent(new HandleCreateAction() {
            @Override
            public void call(Action action) {
                onSetAction(action);
            }
        }));

        configureIconClick();
    }

    private void endActivity(Goal goal) {
        // go to general goal fragment page (or details page?)
        getIntent().putExtra(KEY_COMPOSED_GOAL, goal);
        // getIntent().putExtra(KEY_COMPOSED_POST_TAGS, tagObjects);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    public interface HandleCreateAction { // fixme -- for now, doesn't save it. depends on later saving logic
        public void call(Action action); // should always call onSetAction
    }

    public void onSetAction(Action action) {
        // adds the task of this action to the recyclerview, adds the action to list unsavedActions

        Log.i(TAG, "passing info of action back to compose activity");

        unsavedActions.add(action);
        actionAdapter.notifyItemInserted(unsavedActions.size() - 1);
    }

    private void configureIconClick() {
        cvIcon.setOnClickListener((v) -> {
            String term = etGoalName.getText().toString().toLowerCase();
            if (term.equals("")) {
                Toast.makeText(this, "Give your group a name first!", Toast.LENGTH_SHORT).show();
                return;
            }

            View view = getLayoutInflater().inflate(R.layout.layout_recycler_view, null);
            RecyclerView rv = view.findViewById(R.id.rv);

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Choose an icon")
                    .setView(view)
                    .setNegativeButton("Cancel", (dialog, which) -> {})
                    .create();

            List<NounProjectClient.Icon> icons = new ArrayList<>();
            DataComponentAdapter<NounProjectClient.Icon> adapter = new DataComponentAdapter<NounProjectClient.Icon>(this, icons) {
                @Override
                protected Component makeComponent(NounProjectClient.Icon item, Component.ViewHolder holder) {
                    ImageComponent component = new ImageComponent(Icons.highestResImage(item));
                    component.setOnClick(() -> {
                        alertDialog.dismiss();
                        goal.setNounIcon(item);
                        Icons.from(GoalComposeActivity.this).displayNounIcon(item, cvIcon, ivIcon);
                    });
                    return component;
                }

                @Override
                protected Component.Inflater makeInflater(NounProjectClient.Icon item) {
                    return new ImageComponent.Inflater();
                }
            };

            rv.setLayoutManager(new GridLayoutManager(this, 4));
            rv.setAdapter(adapter);
            // TODO: Add padding

            Icons.from(this).nounIcons(term, 20, (suggestedIcons, e) -> {
                runOnUiThread(() -> {
                    if (e != null) {
                        Log.e("GroupComposeActivity", "Failed to load suggested group icons", e);
                        Toast.makeText(this, "Couldn't find any icons for \"" + term + "\"", Toast.LENGTH_LONG).show();
                        // TODO: Create friendlier UI for this
                        // TODO: Differentiate between network errors and no icons found
                        return;
                    }

                    Collections.addAll(icons, suggestedIcons);
                    adapter.notifyDataSetChanged();
                });
            });

            alertDialog.show();
        });
    }
}
