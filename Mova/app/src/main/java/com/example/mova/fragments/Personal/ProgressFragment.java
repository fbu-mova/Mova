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
import com.example.mova.fragments.PersonalFragment;
import com.example.mova.model.Goal;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.jjoe64.graphview.GraphView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    public static String KEY_SWITCH_FRAGMENT = "switchFragment";
    private AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment;

    @BindView(R.id.graphProgress)
    GraphView graph;
    protected List<Goal> mGoals;
    private int length = 0;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and count of parameters
    public static ProgressFragment newInstance(AsyncUtils.ItemCallback<PersonalFragment.FragmentName> switchFragment) {
        ProgressFragment fragment = new ProgressFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_SWITCH_FRAGMENT, Parcels.wrap(switchFragment)); // FIXME: Will this work?
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            switchFragment = Parcels.unwrap(getArguments().getParcelable(KEY_SWITCH_FRAGMENT));
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
        queryGoals(() -> setGraph());
    }

    private void setGraph(){
        AsyncUtils.executeMany(mGoals.size(), (i, callback) -> {
                    mGoals.get(i).createGraph(length,(series) -> {
                        series.setTitle(mGoals.get(i).getTitle());
                        if(mGoals.get(i).getColor() != null){
                            series.setColor(Color.parseColor(mGoals.get(i).getColor()));
                        }
                        graph.addSeries(series);
                    });

                }, () -> Toast.makeText(getContext(), "Created graphs", Toast.LENGTH_SHORT).show()
        );
    }



    public void queryGoals(AsyncUtils.EmptyCallback callback){
        User user = (User) ParseUser.getCurrentUser();
        ParseQuery<Goal> goalQuery = new ParseQuery<Goal>(Goal.class);
        goalQuery.whereEqualTo("usersInvolved", user);
        goalQuery.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                if(e != null){
                    Log.e("ProgressFragment", "Error with query");
                    e.printStackTrace();
                    return;
                }
                //Get all the goals TODO- only add goal if the user is part of it
                mGoals.addAll(objects);
                callback.call();
            }
        });
    }
}
