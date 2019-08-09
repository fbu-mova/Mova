package com.example.mova.fragments.Personal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GoalComposeActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.GoalThumbnailComponent;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.mova.activities.GoalComposeActivity.KEY_COMPOSED_GOAL;
import static com.example.mova.model.Goal.KEY_FROM_GROUP;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalsFragment extends Fragment {

    private static final String TAG = "personal goal fragment";
    private static final int REQUEST_COMPOSE_GOAL = 38;

    private DelegatedResultActivity activity;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.fabComposeGoal)      protected FloatingActionButton fabComposeGoal;

    // thumbnail recyclerview
    @BindView(R.id.rvThumbnailGoals)    protected RecyclerView rvThumbnailGoals;
    private ArrayList<Goal.GoalData> thumbnailGoals;
    private DataComponentAdapter<Goal.GoalData> thumbnailGoalsAdapter;

    // allGoals recyclerview
    @BindView(R.id.rvAllGoals)          protected RecyclerView rvAllGoals;
    private ArrayList<Goal.GoalData> allGoals;
    private DataComponentAdapter<Goal.GoalData> allGoalsAdapter;

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
        thumbnailGoalsAdapter = new DataComponentAdapter<Goal.GoalData>(activity, thumbnailGoals) {
            @Override
            public Component makeComponent(Goal.GoalData item, Component.ViewHolder holder) {
                Component component = new GoalThumbnailComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Goal.GoalData item) {
                return new GoalThumbnailComponent.Inflater();
            }
        };

        // set layout manager
        rvThumbnailGoals.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        // set adapter to recyclerview
        rvThumbnailGoals.setAdapter(thumbnailGoalsAdapter);

        // set edge decorator
        rvThumbnailGoals.addItemDecoration(new EdgeDecorator((int) getResources().getDimension(R.dimen.innerMargin), EdgeDecorator.Orientation.Horizontal));

        // load thumbnail goals into recyclerview
        Log.d(TAG, "in onViewCreated");
        loadThumbNailGoals();

        // allGoals
        allGoals = new ArrayList<>();

        allGoalsAdapter = new DataComponentAdapter<Goal.GoalData>(activity, allGoals) {
            @Override
            public Component makeComponent(Goal.GoalData item, Component.ViewHolder holder) {
                Component component = new GoalCardComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Goal.GoalData item) {
                return new GoalCardComponent.Inflater();
            }
        };

        rvAllGoals.setLayoutManager(new LinearLayoutManager(activity));
        rvAllGoals.setAdapter(allGoalsAdapter);
        rvAllGoals.addItemDecoration(new EdgeDecorator((int) getResources().getDimension(R.dimen.innerMargin)));

        Log.d(TAG, "about to call loadAllGoals");
        loadAllGoals();
    }

    private void loadThumbNailGoals() {
        // todo -- create an algorithm that decides which posts will be featured here
            // for now, just do normal loadAllGoals

        ParseQuery<Goal> allGoalsQuery = (User.getCurrentUser())
                .relGoals
                .getQuery()
                //.setLimit(5)
                .include(KEY_FROM_GROUP)
                .orderByDescending(Goal.KEY_CREATED_AT);

        updateAdapter(allGoalsQuery, thumbnailGoals, thumbnailGoalsAdapter, rvThumbnailGoals);
    }

    private void loadAllGoals() {

        ParseQuery<Goal> allGoalsQuery = (User.getCurrentUser())
                .relGoals
                .getQuery()
                .setLimit(3)
                .include(KEY_FROM_GROUP)
                .orderByDescending(Goal.KEY_UPDATED_AT);

        updateAdapter(allGoalsQuery, allGoals, allGoalsAdapter, rvAllGoals);
    }

    private void updateAdapter(ParseQuery<Goal> goalsQuery, ArrayList<Goal.GoalData> goals, DataComponentAdapter<Goal.GoalData> goalsAdapter, RecyclerView rvGoals) {
        // todo -- add refresh/loading capabilities

        goalsQuery.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                getActivity().runOnUiThread(() -> {
                    if (e == null) {
                        Log.d(TAG, "Goal query succeeded!");
                        Log.d(TAG, String.format("object size: %s", objects.size()));

                        for (int i = objects.size() - 1; i >= 0; i--) {
                            // load into recyclerview
                            // FIXME: Insert an object that stores the goal + the async boolean
                            // FIXME: (alt) Update userIsInvolved on User object
                            Goal goal = objects.get(i);
                            Goal.GoalData data = new Goal.GoalData(goal, true);
                            // for personal goal fragment, querying means user involved with all resulting goals
                            goals.add(0, data);
                            goalsAdapter.notifyItemInserted(0);

                            rvGoals.scrollToPosition(0);
                        }

                    } else {
                        Log.e(TAG, "goal query failed", e);
                    }
                });
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

                // since user just composed, goal should be in its relGoals
                Goal.GoalData wrap = new Goal.GoalData(goal, true);

                // update recyclerviews
                allGoals.add(0, wrap);
                allGoalsAdapter.notifyItemInserted(0);

                thumbnailGoals.add(0, wrap);
                thumbnailGoalsAdapter.notifyItemInserted(0);

                rvAllGoals.scrollToPosition(0);
                rvThumbnailGoals.scrollToPosition(0);
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
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
////        if (context instanceof OnFragmentInteractionListener) {
////            mListener = (OnFragmentInteractionListener) context;
////        } else {
////            throw new RuntimeException(context.toString()
////                    + " must implement OnFragmentInteractionListener");
////        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
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

