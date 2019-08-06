package com.example.mova.fragments.Social;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.GroupThumbnailComponent;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.FriendUtils;
import com.example.mova.utils.GroupUtils;
import com.example.mova.utils.TagUtlis;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ExploreFragment extends Fragment {

    User user;

    @BindView(R.id.tvExploreTag)
    TextView tvExploreTag;

    @BindView(R.id.rvInto) RecyclerView rvInto;
    List<Group> groupsInto;
    DataComponentAdapter<Group> groupsIntoAdapter;

    @BindView(R.id.rvFriendGroups) RecyclerView rvFriendGroups;
    List<Group> friendGroups;
    DataComponentAdapter<Group> friendGroupsAdapter;

    @BindView(R.id.rvSomethingNew) RecyclerView rvSomethingNew;
    //List<Group> newGroups;
    //DataComponentAdapter<Group> newGroupsAdapter;


    //@BindView(R.id.clTest) protected ComponentLayout clTest;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ExploreFragment.
     */
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
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
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        user = User.getCurrentUser();
        groupsInto = new ArrayList<>();
        friendGroups = new ArrayList<>();
        //newGroups = new ArrayList<>();

        groupsIntoAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), groupsInto) {
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

        friendGroupsAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), friendGroups) {
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

//        newGroupsAdapter = new DataComponentAdapter<Group>((DelegatedResultActivity) getActivity(), newGroups) {
//            @Override
//            protected Component makeComponent(Group item, Component.ViewHolder holder) {
//                Component component = new GroupThumbnailComponent(item);
//                return component;
//            }
//
//            @Override
//            protected Component.Inflater makeInflater(Group item) {
//                return new GroupThumbnailComponent.Inflater();
//            }
//        };

        rvInto.setLayoutManager(new GridLayoutManager(getContext(), 1,  GridLayoutManager.HORIZONTAL,false));
        rvFriendGroups.setLayoutManager(new GridLayoutManager(getContext(), 1,  GridLayoutManager.HORIZONTAL,false));
        //rvSomethingNew.setLayoutManager(new GridLayoutManager(getContext(), 2,  GridLayoutManager.HORIZONTAL,false));

        rvInto.setAdapter(groupsIntoAdapter);
        rvFriendGroups.setAdapter(friendGroupsAdapter);
        //rvSomethingNew.setAdapter(newGroupsAdapter);

        GroupUtils.getUserGroups(user, (groupList) -> {
            if(groupList.size() != 0) {
                int i = (int) Math.round(Math.random() * (groupList.size() - 1));
                Log.i("RandomExploreNumber", String.valueOf(i));
                Group randomGroup = groupList.get(i);
                GroupUtils.getTags(randomGroup, (tags) -> {
                    if(tags.size() != 0) {
                        int j = (int) Math.round(Math.random() * (tags.size() - 1));
                        Tag tag = tags.get(j);
                        tvExploreTag.setText("Because you're into " + tag.getName());
                        TagUtlis.getGroups(tag, (tagGroups) -> {
                            groupsInto.addAll(tagGroups);
                            groupsIntoAdapter.notifyDataSetChanged();
                            rvInto.scrollToPosition(0);
                        });
                    }
                });
            }
        });

        FriendUtils.queryFriends(user, (friendList) -> {
            AsyncUtils.executeMany(friendList.size(), (i, cb) -> {
               User friend = friendList.get(i);
               //TODO - Figure out what userGroups wont enter callback
               GroupUtils.getUserGroups(friend, (friendInGroups) -> {
                  for(int k = 0; k < friendInGroups.size(); k++ ){
                      if(friendGroups.size() == 0){
                          friendGroups.add(friendInGroups.get(0));
                          cb.call(null);
                      }
                      for(int l = 0; l < friendGroups.size(); l++){
                          if(!(friendGroups.get(k).equals(friendInGroups.get(l)))){
                              friendGroups.add(friendInGroups.get(l));
                              cb.call(null);

                          }
                      }
                   }
               });
            }, (e) -> {
                friendGroupsAdapter.notifyDataSetChanged();
                rvFriendGroups.scrollToPosition(0);
            });
        });


    }

}
