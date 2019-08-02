package com.example.mova.fragments.Personal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.EditProfileDialog;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.LoginActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.ProfileFriendComponent;
import com.example.mova.components.ProfileGroupComponent;
import com.example.mova.components.ProfileShowMoreGroupsComponent;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.scrolling.EdgeDecorator;
import com.example.mova.utils.FriendUtils;
import com.example.mova.utils.GroupUtils;
import com.parse.ParseFile;
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
    @BindView(R.id.btnEditProfile) protected Button btnEditProfile;
    @BindView(R.id.tvUsername) protected TextView tvUsername;
    @BindView(R.id.tvShowGroups) protected TextView tvShowGroups;
    @BindView(R.id.tvShowFriends) protected TextView tvShowFriends;
    @BindView(R.id.ivProfilePic) protected ImageView ivProfilePic;

    private DataComponentAdapter<Group> showMoreGroupAdapter;

    @BindView(R.id.rvGroups) protected RecyclerView rvGroups;
    protected List<Group> userGroups;
    private DataComponentAdapter<Group> userGroupAdapter;

    @BindView(R.id.rvFriendsExtra) protected RecyclerView rvFriends;
    protected List<User> userFriends;
    private DataComponentAdapter<User> userFriendAdapter;

    @BindView(R.id.rvPosts) protected RecyclerView rvPosts;
    protected List<Post> userPosts;
    private DataComponentAdapter<Post> userPostAdapter;

    private User user;
    public static FragmentManager manager;
//    private GroupUtils groupUtils;
//    private FriendUtils friendUtils;

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
        user = User.getCurrentUser();
        userGroups = new ArrayList<>();
        userFriends = new ArrayList<>();
        userPosts = new ArrayList<>();
        manager = getFragmentManager();
//        groupUtils = new GroupUtils();
//        friendUtils = new FriendUtils();

        tvUsername.setText(user.getUsername());
        ParseFile file = user.getProfilePic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(ivProfilePic);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String username = currentUser.getUsername();

                ParseUser.logOut();
                currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                getActivity().finish();
            }
        });
        //Todo - implement profile edit

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileDialog epdialog = EditProfileDialog.newInstance();
//                Dialog dialog = epdialog.onCreateDialog(savedInstanceState);
                epdialog.show(manager,"Edit_Profile" );
            }
        });

        userGroupAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), userGroups) {
            @Override
            public Component makeComponent(Group item, Component.ViewHolder holder) {
                Component component = new ProfileGroupComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Group item) {
                return new ProfileGroupComponent.Inflater();
            }
        };

        userFriendAdapter = new DataComponentAdapter<User>((DelegatedResultActivity) getActivity(), userFriends) {
            @Override
            public Component makeComponent(User item, Component.ViewHolder holder) {
                Component component = new ProfileFriendComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(User item) {
                return new ProfileFriendComponent.Inflater();
            }
        };

        showMoreGroupAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), userGroups) {
            @Override
            public Component makeComponent(Group item, Component.ViewHolder holder) {
                Component component = new ProfileShowMoreGroupsComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Group item) {
                return new ProfileShowMoreGroupsComponent.Inflater();
            }
        };




        rvGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvFriends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvGroups.setAdapter(userGroupAdapter);
        rvFriends.setAdapter(userFriendAdapter);

        tvShowFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - Make this work
                EdgeDecorator decorator = new EdgeDecorator(0);
                //Toast.makeText(getContext(), "We made it", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_rv_profile_friends, null  );
                RecyclerView rvExtraFriends = view.findViewById(R.id.rvFriendsExtra);
                rvExtraFriends.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                rvExtraFriends.setAdapter(userFriendAdapter);
                rvExtraFriends.addItemDecoration(decorator);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Friends")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setView(view);
                dialog.show();
            }
        });

        tvShowGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdgeDecorator decorator = new EdgeDecorator(10);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view1 = inflater.inflate(R.layout.layout_rv_profile_friends, null);
                RecyclerView rvExtraGroups = view1.findViewById(R.id.rvFriendsExtra);
                rvExtraGroups.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvExtraGroups.setAdapter(showMoreGroupAdapter);
                rvExtraGroups.addItemDecoration(decorator);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Groups")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setView(view1);
                dialog.show();
            }
        });

        GroupUtils.queryGroups(user, (groups) -> {
            userGroups.addAll(groups);
            userGroupAdapter.notifyDataSetChanged();
            rvGroups.scrollToPosition(0);


        });

        FriendUtils.queryFriends(user, (friends) -> {
            userFriends.addAll(friends);
            userFriendAdapter.notifyDataSetChanged();
            rvFriends.scrollToPosition(0);
        });
    }
}
