package com.example.mova.fragments.Personal;

import android.content.Context;
import android.graphics.Color;
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
import com.example.mova.adapters.ComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.ProgressGoalComponent;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgressFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    @BindView(R.id.graphProgress)
    GraphView graph;
    @BindView(R.id.rvWell)
    RecyclerView rvWell;
    @BindView(R.id.rvWork) RecyclerView rvWork;
    protected List<Goal> mGoals;
    protected List<Goal> goodGoals;
    protected List<Goal> badGoals;
    //protected TreeSet<Prioritized<Goal>> prioGoals;
    private int length = 0;
    private ComponentAdapter<Goal> goalsWellAdapter;
    private ComponentAdapter<Goal> goalsWorkAdaper;
    GoalUtils goalUtils;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        length = 7;
        mGoals = new ArrayList<>();
        goodGoals = new ArrayList<>();
        badGoals = new ArrayList<>();
        //prioGoals = new TreeSet<>();
        goalUtils = new GoalUtils();

        //create the adapter

        goalsWellAdapter = new ComponentAdapter<Goal>(getActivity(), goodGoals) {
            @Override
            public Component<Goal> makeComponent(Goal item) {
                Component<Goal> component = new ProgressGoalComponent(item);
                return component;
            }
        };
        goalsWorkAdaper = new ComponentAdapter<Goal>(getActivity(), badGoals) {
            @Override
            public Component<Goal> makeComponent(Goal item) {
                Component<Goal> component = new ProgressGoalComponent(item);
                return component;
            }
        };
        rvWell.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvWork.setLayoutManager(new LinearLayoutManager(getActivity()));

        rvWell.setAdapter(goalsWellAdapter);
        rvWork.setAdapter(goalsWorkAdaper);



        queryGoals(() -> setGraph(() -> {
            Calendar cal = Calendar.getInstance();
            Date d1 = cal.getTime();
            cal.add(Calendar.DATE, -length + 1);
            Date d2 = cal.getTime();
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(),new SimpleDateFormat("MM/dd")));
            graph.getViewport().setMinX(Long.valueOf(d2.getTime()).doubleValue());
            graph.getViewport().setMaxX(Long.valueOf(d1.getTime()).doubleValue());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHumanRounding(false);
            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
            //graph.getGridLabelRenderer().setVerticalAxisTitle("Actions Completed");


            Toast.makeText(getContext(), "Graph created", Toast.LENGTH_SHORT).show();
            Log.e("ProgressFragment", "Were not in boys");

            //organize goals and create adapter
            goalUtils.sortGoals(mGoals, length, (User) ParseUser.getCurrentUser(), (tsGoals) -> {
                Log.e("ProgressFragment", "Were in boys");
                Toast.makeText(getContext(), "We entered", Toast.LENGTH_SHORT).show();
                for(int i = 0; i < mGoals.size(); i++){
                    if(tsGoals.size() == 0) {
                        break;
                    }
                    if(tsGoals.first().value >= 0) {
                        goodGoals.add(tsGoals.first().item);
                        goalsWellAdapter.notifyItemInserted(i);
                        tsGoals.remove(tsGoals.first());
                        if(tsGoals.size() == 0){
                            break;
                        }
                    }
                    if(tsGoals.last().value < 0) {
                        badGoals.add(tsGoals.last().item);
                        goalsWorkAdaper.notifyItemInserted(i);
                        tsGoals.remove(tsGoals.last());
                    }

                }
                rvWell.scrollToPosition(0);
                rvWork.scrollToPosition(0);

            });



        }));




    }

    private void setGraph(AsyncUtils.EmptyCallback callback){
        AsyncUtils.executeMany(mGoals.size(), (i,cb) -> {
            //todo
           Goal goal = mGoals.get(i);
            goalUtils.getDataForGraph(goal, (User) ParseUser.getCurrentUser(), length , (series) -> {
                series.setTitle(goal.getTitle());
                if(goal.getColor() != null){
                    series.setColor(Color.parseColor(goal.getColor()));
                }
                graph.addSeries(series);
                cb.call(null);
//                        graph.getViewport().setMinX(-1*length);
            });

        }, () -> {callback.call();});

    }


    public void queryGoals(AsyncUtils.EmptyCallback callback){
        User user = (User) ParseUser.getCurrentUser();
        ParseQuery<Goal> goalQuery = new ParseQuery<Goal>(Goal.class);
        goalQuery.whereEqualTo("usersInvolved", user);
        //Log.d("ProgressFragment", "About to query");
        goalQuery.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                //Log.d("ProgressFragment", "querying");
                if(e != null){
                    Log.e("ProgressFragment", "Error with query");
                    e.printStackTrace();
                    return;
                }
                //Get all the goals TODO- only add goal if the user is part of it
                mGoals.addAll(objects);
                //Log.d("ProgressFragment", "Finish with query, the size of mGoals is" + mGoals.size());
                callback.call();
            }
        });
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
