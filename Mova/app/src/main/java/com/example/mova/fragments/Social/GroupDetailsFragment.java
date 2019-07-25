package com.example.mova.fragments.Social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.PostComponent;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.utils.GroupUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 interface
 * to handle interaction events.
 * Use the {@link GroupDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupDetailsFragment extends Fragment {

    Group group;

    @BindView(R.id.bottom_navigation_groups)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.svGroupDetails)
    SearchView svGroupDetails;
    @BindView(R.id.ivGroupDetailsPic)
    ImageView ivGroupDetailsPic;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;

    protected List<Post> groupPosts;
    protected List<Goal> groupGoals;

    private DataComponentAdapter<Post> groupPostAdapter;
    private DataComponentAdapter<Goal> groupGoalAdapter;

    @BindView(R.id.rvGroupGoals)
    RecyclerView rvGroupGoals;
    @BindView(R.id.rvGroupPosts)
    RecyclerView rvGroupPosts;

    public GroupDetailsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static GroupDetailsFragment newInstance(Group item) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("group", item);
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
        return inflater.inflate(R.layout.fragment_group_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        group = this.getArguments().getParcelable("group");
        groupGoals = new ArrayList<>();
        groupPosts = new ArrayList<>();

        tvGroupName.setText(group.getName());
        ParseFile file = group.getGroupPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(ivGroupDetailsPic);
        }


        rvGroupGoals.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvGroupPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        groupGoalAdapter = new DataComponentAdapter<Goal>((DelegatedResultActivity) getActivity(), groupGoals) {
            @Override
            public Component makeComponent(Goal item) {
                Component component = new GoalCardComponent(item);
                return component;
            }
        };

        groupPostAdapter = new DataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), groupPosts) {
            @Override
            public Component makeComponent(Post item) {
                Component component = new PostComponent(item);
                return component;
            }
        };


        rvGroupGoals.setAdapter(groupGoalAdapter);
        rvGroupPosts.setAdapter(groupPostAdapter);

        GroupUtils.getGroupGoals(group, (goals) -> {
            groupGoals.addAll(goals);
            groupGoalAdapter.notifyDataSetChanged();
            rvGroupGoals.scrollToPosition(0);
        });

        GroupUtils.getGroupPosts(group, (posts) -> {
            groupPosts.addAll(posts);
            groupPostAdapter.notifyDataSetChanged();
            rvGroupPosts.scrollToPosition(0);
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_group_goals:

                        rvGroupGoals.setVisibility(View.VISIBLE);
                        rvGroupPosts.setVisibility(View.GONE);
                        return true;

                    case R.id.action_group_posts:

                        rvGroupGoals.setVisibility(View.GONE);
                        rvGroupPosts.setVisibility(View.VISIBLE);
                        default:return true;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_group_goals);
    }
}
