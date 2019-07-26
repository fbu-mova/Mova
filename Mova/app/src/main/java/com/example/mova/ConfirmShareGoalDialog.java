package com.example.mova;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mova.activities.GoalDetailsActivity;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GroupUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmShareGoalDialog extends DialogFragment {

    private static final String TAG = "confirmShareGoal";

    private Goal goal;
//    private Activity activity; // fixme -- assumes coming from goaldetailsactivity, may be harder coming from a goal component

    @BindView(R.id.tvGoalName)      protected TextView tvGoalName;
    @BindView(R.id.groupSpinner)    protected Spinner groupSpinner;
    @BindView(R.id.etDescription)   protected EditText etDescription;
    @BindView(R.id.btShare)         protected Button btShare;

    public ConfirmShareGoalDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ConfirmShareGoalDialog newInstance(Goal goal) {
        ConfirmShareGoalDialog frag = new ConfirmShareGoalDialog();
        Bundle args = new Bundle();
        args.putParcelable("goal", goal);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_share_goal, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        goal = getArguments().getParcelable("goal");

        tvGoalName.setText(goal.getTitle());

        // set up spinner

        // create spinner data
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Friends");

        GroupUtils.queryGroups((User) ParseUser.getCurrentUser(), new AsyncUtils.ListCallback<Group>() {
            @Override
            public void call(List<Group> groupsList) {
                for (int i = 0; i < groupsList.size(); i++ ) {
                    spinnerArray.add(groupsList.get(i).getName());
                }
            }
        });

        // set spinner adapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(spinnerArrayAdapter);

        // set up button
        btShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chosenGroup = (groupSpinner.getSelectedItem() != null) ? groupSpinner.getSelectedItem().toString() : null;
                if (chosenGroup != null) {
                    // continue sharing process
                    String description = etDescription.getText().toString();
                    shareGoal(description, chosenGroup);
                }
                else {
                    Toast.makeText(getActivity(), "Choose a group first!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void shareGoal(String description, String groupName) {

        Toast.makeText(getContext(), "you made it", Toast.LENGTH_LONG).show();

        // TODO lots of async stuff:
            // find group to share it in (or just friends, which is null group)
            // save goal to group's goal relation (as a goal)
            // save post of goal to group's post relation
                // (everything that shows up in social is as a post w/ embedded media)
                // can query for goals by querying equality via embedded media
            // save updated goal isPersonal boolean
            // save updated goal fromGroup pointer to group -- the fact that they posted it is stored in post version of goal

//        goal.setIsPersonal(true);
//        goal.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.d(TAG, "shared goal successfully");
//                    Toast.makeText(getContext(), "Shared goal!", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Log.e(TAG, "sharing goal failed");
//                }
//            }
//        });

    }
}
