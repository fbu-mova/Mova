package com.example.mova.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mova.R;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GroupUtils;
import com.example.mova.utils.MediaUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.mova.model.Group.KEY_NAME;

public class ConfirmShareGoalDialog extends DialogFragment {

    private static final String TAG = "confirmShareGoal";

    private Goal goal;
//    private Activity activity; // fixme -- assumes coming from goaldetailsactivity, may be harder coming from a goal component

    @BindView(R.id.tvGoalName)      protected TextView tvGoalName;
    @BindView(R.id.groupSpinner)    protected Spinner groupSpinner;
    @BindView(R.id.etDescription)   protected EditText etDescription;
    @BindView(R.id.btShare)         protected Button btShare;
    @BindView(R.id.ivClose)         protected ImageView ivClose;

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

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // closes the dialog
                dismiss();
            }
        });

        // set up spinner

        // create spinner data
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Friends");

        GroupUtils.queryGroups(User.getCurrentUser(), new AsyncUtils.ListCallback<Group>() {
            @Override
            public void call(List<Group> groupsList) {
                for (int i = 0; i < groupsList.size(); i++ ) {
                    spinnerArray.add(groupsList.get(i).getName());
                }
            }
        }); // fixme -- here and later group finding logic assumes all groups will have diff names, since that's how we find group again (can store locally?)

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

    // TODO lots of async stuff: SHARING A GOAL
    // find group to share it in (or just friends, which is null group)
    // save goal to group's goal relation (as a goal)
    // create + save post of goal, possible to group's post relation if group exists
    // (everything that shows up in social is as a post w/ embedded media)
    // can query for goals by querying equality via embedded media
    // save updated goal isPersonal boolean
    // save updated goal fromGroup pointer to group -- the fact that they posted it is stored in post version of goal

    // TODO -- save goal to group relation of goals

    // TODO -- create specific use case of "bi"-directional (or just multi-directional) goal saving:
    // want to save post of goal to Group's posts

    // TODO -- update fromGroup pointer in the goal

    private void shareGoal(String description, String groupName) {

        findParentGroup(description, groupName);

    }

    private void findParentGroup(String description, String groupName) {

        if (groupName == "Friends") {
            // only need to make post
            makeGoalPost(description);
        }
        else {
            // need to find group, then make post

            ParseQuery<Group> groupQuery = User.getCurrentUser().relGroups.getQuery()
                    .whereEqualTo(KEY_NAME, groupName);
            groupQuery.findInBackground(new FindCallback<Group>() {
                @Override
                public void done(List<Group> objects, ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Successfully found group");
                        if (objects.size() == 1) {
                            makeGoalPost(description, objects.get(0));
                        }
                        else {
                            Log.e(TAG, "Found two groups with the same name");
                        }
                    }
                }
            });
        }

    }

    private void makeGoalPost(String description) {
        // same but don't set group

        User user = User.getCurrentUser();

        Post goalPost = new Post();
        goalPost.setAuthor(user)
                .setBody(description)
                .setIsPersonal(false);

        saveGoalPost(goalPost, null);
    }

    private void makeGoalPost(String description, Group group) {
        // makes the post of a goal, saves this post to the User's post relation

        // fixme -- did not include location of user nor mood
        User user = User.getCurrentUser();

        Post goalPost = new Post();
        goalPost.setAuthor(user)
                .setBody(description)
                .setIsPersonal(false)
                .setGroup(group);

        saveGoalPost(goalPost, group);

    }

    private void saveGoalPost(Post goalPost, Group group) {

        goalPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Saved newly made goal post");

                    // add to user's post relation
                    User.getCurrentUser().relPosts.add(goalPost, (post) -> {
                        // go to next step:
                        updatePostMedia(post, group);
                    });
                }
                else {
                    Log.e(TAG, "Saving goal post failed", e);
                    Toast.makeText(getContext(), "Sharing your goal didn't work this time, try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void updatePostMedia(Post post, Group group) {
        // creates media instance to store goal, then put back in post and save

        MediaUtils.updateMediaToPost(post, goal, new AsyncUtils.ItemCallback<Object>() {
            @Override
            public void call(Object item) {
                updateParentGroup(post, group);
            }
        });

    }

    private void updateParentGroup(Post post, Group group) {

        if (group != null) {
            // update group's goals and posts, then save
            group.relGoals.add(goal);
            group.relPosts.add(post);
            group.relUsers.add(User.getCurrentUser());

            group.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "updated group");
                    }
                    else {
                        Log.e(TAG, "updating group failed", e);
                    }
                }
            });
        }

        saveGoal(group);

    }

    private void saveGoal(Group group) {

        goal.setIsPersonal(false);

        if (group != null) {
            goal.setGroup(group);
        }

        goal.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "shared goal successfully");
                    Toast.makeText(getContext(), "Shared goal!", Toast.LENGTH_SHORT).show();
                    ConfirmShareGoalDialog.this.dismiss(); // fixme -- add this as a callback if want to extract
                }
                else {
                    Log.e(TAG, "sharing goal failed");
                }
            }
        });

    }
}
