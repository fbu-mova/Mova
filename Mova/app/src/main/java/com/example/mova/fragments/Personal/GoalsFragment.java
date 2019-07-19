package com.example.mova.fragments.Personal;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.GoalThumbnailComponent;
import com.example.mova.model.Goal;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoalsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoalsFragment extends Fragment {

    private static final String TAG = "personal goal fragment";

    private Activity activity;

    // thumbnail recyclerview
    @BindView(R.id.rvThumbnailGoals)    protected RecyclerView rvThumbnailGoals;
    private ArrayList<Goal> thumbnailGoals;
    private ComponentAdapter<Goal> thumbnailGoalsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GoalsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoalsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoalsFragment newInstance(String param1, String param2) {
        GoalsFragment fragment = new GoalsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        ButterKnife.bind(this, activity);
        thumbnailGoals = new ArrayList<>();

        // assigns the adapter w/ anonymous class
        thumbnailGoalsAdapter = new ComponentAdapter<Goal>(activity, thumbnailGoals) {
            @Override
            public Component<Goal> makeComponent(Goal item) {
                Component<Goal> component = new GoalThumbnailComponent(item);
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
    }

    private void loadThumbNailGoals() {
        // todo -- create an algorithm that decides which posts will be featured here
            // todo -- possible querying in Goal model class
            // fixme -- for now, just do normal loadAllGoals
        Log.d(TAG, "in loadThumbNailGoals");
        loadAllGoals();
    }

    private void loadAllGoals() {
        Log.d(TAG, "in loadAllGoals");
        Goal.Query allGoalsQuery = new Goal.Query();
        allGoalsQuery.getTop()
                .withGroup();

        updateAdapter(allGoalsQuery, thumbnailGoals, thumbnailGoalsAdapter, rvThumbnailGoals);
    }

    private void updateAdapter(Goal.Query goalsQuery, ArrayList<Goal> goals, ComponentAdapter<Goal> goalsAdapter, RecyclerView rvGoals) {
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        mListener = null;
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

