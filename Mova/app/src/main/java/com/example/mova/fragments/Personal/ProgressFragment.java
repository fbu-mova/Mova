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

import com.example.mova.R;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
//import com.jjoe64.graphview.GraphView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
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

//    @BindView(R.id.graphProgress)
//    GraphView graph;
//    protected List<Goal> mGoals;
//    private int length = 0;

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
    // TODO: Rename and change types and count of parameters
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
//        ButterKnife.bind(this, view);
//        length = 7;
//        mGoals = new ArrayList<>();
//        queryGoals(() -> setGraph());
    }

//    private void setGraph(){
//        AsyncUtils.executeMany(mGoals.size(), (i, callback) -> {
//                    mGoals.get(i).createGraph(length,(series) -> {
//                        series.setTitle(mGoals.get(i).getTitle());
//                        if(mGoals.get(i).getColor() != null){
//                            series.setColor(Color.parseColor(mGoals.get(i).getColor()));
//                        }
//                        graph.addSeries(series);
//                    });
//
//                }, () -> Toast.makeText(getContext(), "Created graphs", Toast.LENGTH_SHORT).show()
//                );
//
//
//
//
//    }



//    public void queryGoals(AsyncUtils.EmptyCallback callback){
//        User user = (User) ParseUser.getCurrentUser();
//        ParseQuery<Goal> goalQuery = new ParseQuery<Goal>(Goal.class);
//        goalQuery.whereEqualTo("usersInvolved", user);
//        goalQuery.findInBackground(new FindCallback<Goal>() {
//            @Override
//            public void done(List<Goal> objects, ParseException e) {
//                if(e != null){
//                    Log.e("ProgressFragment", "Error with query");
//                    e.printStackTrace();
//                    return;
//                }
//                //Get all the goals TODO- only add goal if the user is part of it
//                mGoals.addAll(objects);
//                callback.call();
//            }
//        });
//    }

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
