package com.example.mova.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.EventThumbnailComponent;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.GroupThumbnailComponent;
import com.example.mova.model.Event;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.TagUtlis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {

    User user;
    List<Tag> tags;

    @BindView(R.id.svSearch)
    SearchView svSearch;

    @BindView(R.id.tvSearchGroups)
    TextView tvSearchGroups;
    @BindView(R.id.rvSearchGroups)
    RecyclerView rvSearchGroups;
    List<Group> tagGroups;
    private DataComponentAdapter<Group> tagGroupsAdapter;

    @BindView(R.id.tvSearchEvents) TextView tvSearchEvents;
    @BindView(R.id.rvSearchEvents) RecyclerView rvSearchEvents;
    List<Event> tagEvents;
    private DataComponentAdapter<Event> tagEventsAdapter;

    @BindView(R.id.tvSearchGoals) TextView tvSearchGoals;
    @BindView(R.id.rvSearchGoals) RecyclerView rvSearchGoals;
    List<Goal> tagGoals;
    List<Goal.GoalData> tagGoalsData;
    private DataComponentAdapter<Goal.GoalData> tagGoalsAdapter;

    @BindView(R.id.tvClick)
    TextView tvClick;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.activity_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);



        user = User.getCurrentUser();

        svSearch.setSubmitButtonEnabled(true);
        svSearch.onActionViewExpanded();

        tvClick.setVisibility(View.VISIBLE);

        tvSearchGroups.setVisibility(View.GONE);
        tvSearchEvents.setVisibility(View.GONE);
        tvSearchGoals.setVisibility(View.GONE);

        rvSearchGroups.setVisibility(View.GONE);
        rvSearchEvents.setVisibility(View.GONE);
        rvSearchGoals.setVisibility(View.GONE);

        tags = new ArrayList<>();
        tagGroups = new ArrayList<>();
        tagEvents = new ArrayList<>();
        tagGoals = new ArrayList<>();
        tagGoalsData = new ArrayList<>();

        tagGroupsAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), tagGroups) {
            @Override
            protected Component makeComponent(Group item, Component.ViewHolder holder) {
                Component component = new GroupThumbnailComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Group item) {
                return new GroupThumbnailComponent.Inflater();
            }
        };

        tagEventsAdapter = new DataComponentAdapter<Event>((DelegatedResultActivity) getActivity(), tagEvents) {
            @Override
            protected Component makeComponent(Event item, Component.ViewHolder holder) {
                Component component = new EventThumbnailComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Event item) {
                return new EventThumbnailComponent.Inflater();
            }
        };

        tagGoalsAdapter = new DataComponentAdapter<Goal.GoalData>((DelegatedResultActivity) getActivity(), tagGoalsData) {
            @Override
            protected Component makeComponent(Goal.GoalData item, Component.ViewHolder holder) {
                Component component = new GoalCardComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Goal.GoalData item) {
                return new GoalCardComponent.Inflater();
            }
        };

        rvSearchGroups.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvSearchEvents.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvSearchGoals.setLayoutManager(new LinearLayoutManager(getContext()));

        rvSearchGroups.setAdapter(tagGroupsAdapter);
        rvSearchEvents.setAdapter(tagEventsAdapter);
        rvSearchGoals.setAdapter(tagGoalsAdapter);




        TagUtlis.getTags((listoftags) -> {
            tags.addAll(listoftags);

            //getData("Tag");

            svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    tvClick.setVisibility(View.GONE);
                    getData(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        });




        tvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finishAfterTransition();
            }
        });





    }

    private void getData(String tagName){
        Tag tag = new Tag();
        for(Tag tag1: tags){
            if(tag1.getName().equals(tagName)){
                tag = tag1;
                break;
            }
        }

        TagUtlis.getGroups(tag, (listOfGroups) -> {
            tagGroups.addAll(listOfGroups);
            tagGroupsAdapter.notifyDataSetChanged();
            tvSearchGroups.setVisibility(View.VISIBLE);
            rvSearchGroups.setVisibility(View.VISIBLE);
        });

        TagUtlis.getEvents(tag, (listOfEvents) -> {
            tagEvents.addAll(listOfEvents);
            tagEventsAdapter.notifyDataSetChanged();
            tvSearchEvents.setVisibility(View.VISIBLE);
            rvSearchEvents.setVisibility(View.VISIBLE);
        });

        TagUtlis.getGoals(tag,(listOfGoals) -> {
            AsyncUtils.executeMany(listOfGoals.size(), (i2, cb2) -> {
                GoalUtils.checkIfUserInvolved(listOfGoals.get(i2), user, (bool) -> {
                    Goal.GoalData goalData = new Goal.GoalData(listOfGoals.get(i2), bool);
                    tagGoalsData.add(goalData);
                    tagGoalsAdapter.notifyItemInserted(i2);
                    cb2.call(null);
                });
            }, (e) -> {
                tvSearchGoals.setVisibility(View.VISIBLE);
                rvSearchGoals.setVisibility(View.VISIBLE);
            });
        });
    }
}
