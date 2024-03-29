package com.example.mova.fragments.Social;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.GroupComposeActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.EventThumbnailComponent;
import com.example.mova.components.GoalCardComponent;
import com.example.mova.components.PostComponent;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.model.Event;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.EventUtils;
import com.example.mova.utils.GoalUtils;
import com.example.mova.utils.GroupUtils;
import com.example.mova.utils.PostConfig;
import com.example.mova.views.EdgeFloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
    //Todo - fix this

    Group group;
    User user;

    @BindView(R.id.bottom_navigation_groups)
    BottomNavigationView bottomNavigationView;
//    @BindView(R.id.svGroupDetails)
//    SearchView svGroupDetails;
    @BindView(R.id.ivGroupDetailsPic)
    ImageView ivGroupDetailsPic;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.btnJoinGroup)
    Button btnJoinGroup;

    @BindView(R.id.efabCompose) protected EdgeFloatingActionButton efabCompose;

    protected List<Post> groupPosts;
    protected List<Goal.GoalData> groupGoals;
    protected List<Event> groupEvents;


    private DataComponentAdapter<Post> groupPostAdapter;
    private DataComponentAdapter<Goal.GoalData> groupGoalAdapter;
    private DataComponentAdapter<Event> groupEventAdapter;

    @BindView(R.id.rvGroupGoals)
    RecyclerView rvGroupGoals;
    @BindView(R.id.rvGroupPosts)
    RecyclerView rvGroupPosts;
    @BindView(R.id.rvGroupEvents)
    RecyclerView rvGroupEvents;

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
        user = User.getCurrentUser();
        group = this.getArguments().getParcelable("group");
        groupGoals = new ArrayList<>();
        groupPosts = new ArrayList<>();
        groupEvents = new ArrayList<>();

        GroupUtils.getIsAdmin(user, group, (isAdmin) -> {
            if(isAdmin) {
                btnJoinGroup.setText("EDIT GROUP");
                btnJoinGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), GroupComposeActivity.class);
                        intent.putExtra("group", group);
                        startActivity(intent);
                    }
                });
            }else{
                GroupUtils.getIsMember(user,group, (isMember) -> {
                    if(isMember){
                        btnJoinGroup.setText("LEAVE GROUP");
                    }else{
                        btnJoinGroup.setText("JOIN GROUP");
                    }

                    btnJoinGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isMember){
                                group.relUsers.remove(user, () -> {
                                    btnJoinGroup.setText("JOIN GROUP");
                                    user.relGroups.remove(group, () -> {});
                                });
                            }else{
                                group.relUsers.add(user);
                                group.saveInBackground();
                                btnJoinGroup.setText("LEAVE GROUP");
                                user.relGroups.add(group);
                                user.saveInBackground();
                            }
                        }
                    });
                });
            }

        });

        tvGroupName.setText(group.getName());
        ParseFile file = group.getGroupPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.color.purpleLight)
                    .error(R.color.purpleLight)
                    .into(ivGroupDetailsPic);
        }

        efabCompose.setOnClickListener((v) -> {
            PostConfig config = new PostConfig();
            config.post = new Post();
            config.post.setGroup(group);
            config.isPersonal = false;

            new ComposePostDialog.Builder((DelegatedResultActivity) getActivity())
                    .setConfig(config)
                    .setOnPost((post) -> {
                        Toast.makeText(getActivity(), "Posted!", Toast.LENGTH_SHORT).show();
                        groupPosts.add(0, post);
                        groupPostAdapter.notifyItemInserted(0);
                        rvGroupPosts.scrollToPosition(0);
                    })
                    .show(view);
        });

        rvGroupGoals.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvGroupPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvGroupEvents.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        groupGoalAdapter = new DataComponentAdapter<Goal.GoalData>((DelegatedResultActivity) getActivity(), groupGoals) {
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

        groupPostAdapter = new DataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), groupPosts) {
            @Override
            public Component makeComponent(Post item, Component.ViewHolder holder) {
                Component component = new PostComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Post item) {
                return new PostComponent.Inflater();
            }
        };

        groupEventAdapter = new DataComponentAdapter<Event>((DelegatedResultActivity) getActivity(), groupEvents) {
            @Override
            public Component makeComponent(Event item, Component.ViewHolder holder) {
                Component component = new EventThumbnailComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Event item) {
                return new EventThumbnailComponent.Inflater();
            }
        };

        //Todo Merge the rv into one rv and use rv.swapAdapter

        rvGroupGoals.setAdapter(groupGoalAdapter);
        rvGroupPosts.setAdapter(groupPostAdapter);
        rvGroupEvents.setAdapter(groupEventAdapter);

        int margin = getResources().getDimensionPixelOffset(R.dimen.innerMargin);
        int outerMargin = getResources().getDimensionPixelOffset(R.dimen.outerMargin);
        rvGroupGoals.addItemDecoration(new EdgeDecorator.Config(outerMargin, margin)
                .setMode(EdgeDecorator.Mode.Padding)
                .build());
        rvGroupPosts.addItemDecoration(new EdgeDecorator.Config(outerMargin, margin)
                .setMode(EdgeDecorator.Mode.Padding)
                .build());

        //Get group Goals
        GroupUtils.getGroupGoals(group, (goals) -> {

            AsyncUtils.executeMany(goals.size(), (Integer item, AsyncUtils.ItemCallback<Throwable> callback) -> {
                // in for loop
                GoalUtils.checkIfUserInvolved(goals.get(item), (User) ParseUser.getCurrentUser(), (check) -> {
                    Goal.GoalData data = new Goal.GoalData(goals.get(item), check);
                    groupGoals.add(0, data);
                    groupGoalAdapter.notifyItemInserted(0);
                });
            }, () -> {

                rvGroupGoals.scrollToPosition(0);
            });
        });

        //Get group Post
        GroupUtils.getGroupPosts(group, (posts) -> {
            groupPosts.addAll(posts);
            groupPostAdapter.notifyDataSetChanged();
            rvGroupPosts.scrollToPosition(0);

        });

        //Get group Events
        EventUtils.getGroupEvents(group, (events) -> {
            groupEvents.addAll(events);
            groupEventAdapter.notifyDataSetChanged();
            rvGroupEvents.scrollToPosition(0);

        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_group_goals:
                        rvGroupGoals.setVisibility(View.VISIBLE);
                        rvGroupPosts.setVisibility(View.GONE);
                        rvGroupEvents.setVisibility(View.GONE);
                        return true;


                    case R.id.action_group_posts:
                        rvGroupGoals.setVisibility(View.GONE);
                        rvGroupPosts.setVisibility(View.VISIBLE);
                        rvGroupEvents.setVisibility(View.GONE);
                        return true;


                    case R.id.action_group_events:
                        rvGroupGoals.setVisibility(View.GONE);
                        rvGroupPosts.setVisibility(View.GONE);
                        rvGroupEvents.setVisibility(View.VISIBLE);
                        return true;


                    default: return true;
                }
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_group_goals);
    }
}
