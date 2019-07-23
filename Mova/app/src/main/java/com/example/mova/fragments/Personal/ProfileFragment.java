package com.example.mova.fragments.Personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.LoginActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.ProfileFriendComponent;
import com.example.mova.components.ProfileGroupComponent;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.FriendUtils;
import com.example.mova.utils.GroupUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    @BindView(R.id.btnLogout) protected Button btnLogout;
    @BindView(R.id.tvUsername) protected TextView tvUsername;
    @BindView(R.id.tvDescription) protected TextView tvDesciption;
    @BindView(R.id.tvShowGroups) protected TextView tvShowGroups;
    @BindView(R.id.tvShowFriends) protected TextView tvShowFriends;

    @BindView(R.id.rvGroups) protected RecyclerView rvGroups;
    protected List<Group> userGroups;
    private DataComponentAdapter<Group> userGroupAdapter;

    @BindView(R.id.rvFriends) protected RecyclerView rvFriends;
    protected List<User> userFriends;
    private DataComponentAdapter<User> userFriendAdapter;

    @BindView(R.id.rvPosts) protected RecyclerView rvPosts;
    protected List<Post> userPosts;
    private DataComponentAdapter<Post> userPostAdapter;

    private User user;
    private GroupUtils groupUtils;
    private FriendUtils friendUtils;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        user = (User) ParseUser.getCurrentUser();
        userGroups = new ArrayList<>();
        userFriends = new ArrayList<>();
        userPosts = new ArrayList<>();
        groupUtils = new GroupUtils();
        friendUtils = new FriendUtils();

        tvUsername.setText(user.getUsername());


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String username = currentUser.getUsername();

                ParseUser.logOut();
                currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(getContext(), LoginActivity.class);
//                intent.putExtra("username", username);

                startActivity(intent);
                getActivity().finish();
            }
        });

        userGroupAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), userGroups) {
            @Override
            public Component makeComponent(Group item) {
                Component component = new ProfileGroupComponent(item);
                return component;
            }
        };

        userFriendAdapter = new DataComponentAdapter<User>((DelegatedResultActivity) getActivity(), userFriends) {
            @Override
            public Component makeComponent(User item) {
                Component component = new ProfileFriendComponent(item);
                return component;
            }
        };

        rvGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        rvFriends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));

        rvGroups.setAdapter(userGroupAdapter);
        rvFriends.setAdapter(userFriendAdapter);

        groupUtils.queryGroups(user, (groups) -> {
            userGroups.addAll(groups);
            userGroupAdapter.notifyDataSetChanged();
            rvGroups.scrollToPosition(0);
        });

        friendUtils.queryFriends(user, (friends) -> {
            userFriends.addAll(friends);
            userFriendAdapter.notifyDataSetChanged();
            rvFriends.scrollToPosition(0);
        });
    }
}
