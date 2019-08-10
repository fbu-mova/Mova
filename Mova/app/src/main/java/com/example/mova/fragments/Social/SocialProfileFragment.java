package com.example.mova.fragments.Social;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.ProfileFriendComponent;
import com.example.mova.components.ProfileGroupComponent;
import com.example.mova.components.ProfileShowMoreGroupsComponent;
import com.example.mova.icons.Icons;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.FriendUtils;
import com.example.mova.utils.GroupUtils;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SocialProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialProfileFragment extends Fragment {

    //Todo - add friend requests
    //Todo - add edit profile

    @BindView(R.id.ivSocialPic)
    ImageView ivSocialPic;
    @BindView(R.id.cvSocialPic)
    CardView cvSocialPic;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.btnAddFriend)
    Button btnAddFriend;
    @BindView(R.id.tvMoreGroups)
    TextView tvMoreGroups;
    @BindView(R.id.tvMoreFriends)
    TextView tvMoreFriends;
    @BindView(R.id.tvMUTUALFRIENDS)
    TextView tvMUTUALFRIENDS;
    @BindView(R.id.divider2)
    View divider;
    @BindView(R.id.tvPOSTS)
    TextView tvPOSTS;

    private DataComponentAdapter<Group> showMoreGroupAdapter;

    @BindView(R.id.rvSocialGroups)
    RecyclerView rvSocialGroups;
    protected List<Group> userGroups;
    private DataComponentAdapter<Group> userGroupAdapter;

    @BindView(R.id.rvSocialFriends)
    RecyclerView rvSocialFriends;
    protected List<User> userFriends;
    private DataComponentAdapter<User> userFriendAdapter;

    @BindView(R.id.rvSocialPosts)
    RecyclerView rvSocialPosts;
    protected List<Post> userPosts;
    private DataComponentAdapter<Post> userPostAdapter;


    private GroupUtils groupUtils;
    private FriendUtils friendUtils;
    User user;
    User currentUser;



    public SocialProfileFragment() {
        // Required empty public constructor
    }


    public static SocialProfileFragment newInstance(User item) {
        SocialProfileFragment fragment = new SocialProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", item);
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
        return inflater.inflate(R.layout.fragment_social_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        user = this.getArguments().getParcelable("user");
        userGroups = new ArrayList<>();
        userFriends = new ArrayList<>();
        userPosts = new ArrayList<>();
        groupUtils = new GroupUtils();
        friendUtils = new FriendUtils();
        currentUser = User.getCurrentUser();

        Icons.from(getActivity()).displayIdenticon(user,cvSocialPic,ivSocialPic);
        currentUser.isFriendsWith(user, (bool) -> {
            Toast.makeText(getContext(), "Inside", Toast.LENGTH_SHORT).show();
            if(!bool){
                tvMUTUALFRIENDS.setVisibility(View.GONE);
                tvMoreFriends.setVisibility(View.GONE);
                tvPOSTS.setVisibility(View.GONE);
                rvSocialPosts.setVisibility(View.GONE);
                rvSocialFriends.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            }
        });
        btnAddFriend.setText("Friends");
        tvName.setText(user.getUsername());
        ParseFile file = user.getProfilePic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(ivSocialPic);
        }

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




        rvSocialGroups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvSocialFriends.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvSocialGroups.setAdapter(userGroupAdapter);
        rvSocialFriends.setAdapter(userFriendAdapter);

        tvMoreFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - Make this work
                EdgeDecorator decorator = new EdgeDecorator(0);
                //Toast.makeText(getContext(), "We made it", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_recycler_view, null  );
                RecyclerView rvExtraFriends = view.findViewById(R.id.rv);
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

        tvMoreGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EdgeDecorator decorator = new EdgeDecorator(10);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view1 = inflater.inflate(R.layout.layout_recycler_view, null);
                RecyclerView rvExtraGroups = view1.findViewById(R.id.rv);
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

        groupUtils.queryGroups(user, (groups) -> {
            userGroups.addAll(groups);
            userGroupAdapter.notifyDataSetChanged();
            rvSocialGroups.scrollToPosition(0);


        });

        friendUtils.queryFriends(user, (friends) -> {
            AsyncUtils.executeMany(friends.size(), (i,cb) -> {
                User friend = friends.get(i);
                currentUser.isFriendsWith(friend, (bool) -> {
                    if(bool){
                        userFriends.add(friend);
                        userFriendAdapter.notifyDataSetChanged();
                    }
                });
                cb.call(null);
            }, (e) -> {
                    rvSocialFriends.scrollToPosition(0);
            });

        });
    }
}
