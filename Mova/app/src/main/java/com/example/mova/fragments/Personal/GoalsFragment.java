package com.example.mova.fragments.Personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.activities.GoalComposeActivity;
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.GoalThumbnailComponent;
import com.example.mova.model.Goal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.mova.activities.GoalComposeActivity.KEY_COMPOSED_GOAL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalsFragment extends Fragment {

    private static final String TAG = "personal goal fragment";
    private static final int REQUEST_COMPOSE_GOAL = 38;

    private DelegatedResultActivity activity;

    @BindView(R.id.fabComposeGoal)      protected FloatingActionButton fabComposeGoal;

    // thumbnail recyclerview
    @BindView(R.id.rvThumbnailGoals)    protected RecyclerView rvThumbnailGoals;
    private ArrayList<Goal> thumbnailGoals;
    private DataComponentAdapter<Goal> thumbnailGoalsAdapter;

    // allGoals recyclerview
    @BindView(R.id.rvAllGoals)          protected RecyclerView rvAllGoals;
    private ArrayList<Goal> allGoals;
    private DataComponentAdapter<Goal> allGoalsAdapter;

    public GoalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GoalsFragment.
     */
    public static GoalsFragment newInstance() {
        GoalsFragment fragment = new GoalsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = (DelegatedResultActivity) getActivity();

        Log.d(TAG, "in goals fragment");

        ButterKnife.bind(this, activity);

        // set fabComposeGoal onclick listener
        fabComposeGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, GoalComposeActivity.class);
                startActivityForResult(intent, REQUEST_COMPOSE_GOAL);
            }
        });

        // thumbnail
        thumbnailGoals = new ArrayList<>();

        // assigns the adapter w/ anonymous class
        thumbnailGoalsAdapter = new DataComponentAdapter<Goal>(activity, thumbnailGoals) {
            @Override
            public Component makeComponent(Goal item) {
                Component component = new GoalThumbnailComponent(item);
                return component;
            }
        };

        // set layout manager
        rvThumbnailGoals.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        // set adapter to recyclerview
        rvThumbnailGoals.setAdapter(thumbnailGoalsAdapter);

        // load thumbnail goals into recyclerview
        Log.d(TAG, "in onViewCreated");
        loadThumbNailGoals();

        // allGoals
        allGoals = new ArrayList<>();

        allGoalsAdapter = new DataComponentAdapter<Goal>(activity, allGoals) {
            @Override
            public Component makeComponent(Goal item) {
                Component component = new GoalCardComponent(item);
                return component;
            }
        };

        rvAllGoals.setLayoutManager(new LinearLayoutManager(activity));
        rvAllGoals.setAdapter(allGoalsAdapter);

        Log.d(TAG, "about to call loadAllGoals");
        loadAllGoals();
    }

    private void loadThumbNailGoals() {
        // todo -- create an algorithm that decides which posts will be featured here
            // todo -- possible querying in Goal model class
            // fixme -- for now, just do normal loadAllGoals
        Log.d(TAG, "in loadThumbNailGoals");
        Goal.Query allGoalsQuery = new Goal.Query();
        allGoalsQuery.getTop()
                .withGroup()
                .fromCurrentUser();

        updateAdapter(allGoalsQuery, thumbnailGoals, thumbnailGoalsAdapter, rvThumbnailGoals);
    }

    private void loadAllGoals() {
        Log.d(TAG, "in loadAllGoals");
        Goal.Query allGoalsQuery = new Goal.Query();
        allGoalsQuery.getTop()
                .withGroup()
                .fromCurrentUser();

        updateAdapter(allGoalsQuery, allGoals, allGoalsAdapter, rvAllGoals);
    }

    private void updateAdapter(Goal.Query goalsQuery, ArrayList<Goal> goals, DataComponentAdapter<Goal> goalsAdapter, RecyclerView rvGoals) {
        // todo -- add refresh/loading capabilities

        Log.d(TAG, "in updateAdapter");
        goalsQuery.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Goal query succeeded!");
                    Log.d(TAG, String.format("object size: %s", objects.size()));

                    for (int i = 0; i < objects.size(); i++) {
                        // load into recyclerview
                        Goal goal = objects.get(i);
                        goals.add(0, goal); // fixme: order of goals preserved?
                        goalsAdapter.notifyItemInserted(0);
                    }

                    rvGoals.scrollToPosition(0);
                }
                else {
                    Toast.makeText(activity, "Querying for goals failed :(", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Goal query failed", e);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COMPOSE_GOAL) {
                Goal goal = data.getParcelableExtra(KEY_COMPOSED_GOAL);

                // update recyclerviews
                Log.d(TAG, "updating allGoals with intent");
                allGoals.add(0, goal);
                Log.d(TAG, "updating allGoalsAdapter with intent");
                allGoalsAdapter.notifyItemInserted(0);

                Log.d(TAG, "updating thumbnailGoals with intent");
                thumbnailGoals.add(0, goal);
                Log.d(TAG, "updating thumbnailGoalsAdapter with intent");
                thumbnailGoalsAdapter.notifyItemInserted(0);

                rvAllGoals.scrollToPosition(0);
                rvThumbnailGoals.scrollToPosition(0);
                Log.d(TAG, "finished onActivityResult");
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

