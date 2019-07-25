package com.example.mova.fragments.Social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.GroupThumbnailComponent;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.GroupUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {
    //TODO - fix scrolling on goals

    User user;

    @BindView(R.id.svGroups)
    SearchView svGroups;


    @BindView(R.id.rvGroups) RecyclerView rvGroups;
    protected List<Group> userGroups;
    private DataComponentAdapter<Group> groupAdapter;

    @BindView(R.id.rvActiveGoals) RecyclerView rvActiveGoals;
    protected List<Goal> userActiveSocialGoals;
    private DataComponentAdapter<Goal> activeGoalAdaper;



    public GroupsFragment() {
        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        user = (User) ParseUser.getCurrentUser();
        userGroups = new ArrayList<>();
        userActiveSocialGoals = new ArrayList<>();

        groupAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), userGroups) {
            @Override
            public Component makeComponent(Group item) {
                Component component = new GroupThumbnailComponent(item);
                return component;
            }
        };

        activeGoalAdaper = new DataComponentAdapter<Goal>((DelegatedResultActivity) getActivity(), userActiveSocialGoals) {
            @Override
            public Component makeComponent(Goal item) {
                Component component = new GoalCardComponent(item);
                return component;
            }
        };

        rvGroups.setLayoutManager(new GridLayoutManager(getContext(), 2,  GridLayoutManager.HORIZONTAL,false));
        rvActiveGoals.setLayoutManager(new LinearLayoutManager(getContext()));

        rvGroups.setAdapter(groupAdapter);
        rvActiveGoals.setAdapter(activeGoalAdaper);

        GroupUtils.queryGroups(user, (groups) -> {
            userGroups.addAll(groups);
            groupAdapter.notifyDataSetChanged();
            rvGroups.scrollToPosition(0);


        });

        GoalUtils.queryGoals(user, (goals) -> {
            for(Goal goal:goals){
                if(!goal.getIsPersonal()){
                    userActiveSocialGoals.add(0, goal);
                    activeGoalAdaper.notifyItemInserted(0);
                }
            }
            rvActiveGoals.scrollToPosition(0);
        });
    }
}
