package com.example.mova.components;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.fragments.Social.SocialProfileFragment;
import com.example.mova.model.User;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFriendComponent extends Component{

    private static final String TAG = "ProfileFriendComponent";
    private static final int viewLayoutRes = R.layout.item_profile_friend;

    private User user;
    private ProfileFriendViewHolder viewHolder;
    public static FragmentManager manager;

    private ComponentManager componentManager;

    public ProfileFriendComponent(User item){
        super();
        this.user = item;
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new ProfileFriendViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public String getName() {
        return "ProfileFriendComponent";
    }

    @Override
    public void setManager(ComponentManager manager) {
        componentManager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        checkViewHolderClass(holder, ProfileFriendViewHolder.class);
        viewHolder = (ProfileFriendViewHolder) holder;

//        if(viewHolder == null){
//            Log.e(TAG, "Not inflating views in render");
//            return;
//        }

        viewHolder.tvFriendName.setText(user.getUsername());
        ParseFile file = user.getProfilePic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(viewHolder.ivFriendPic);
        }
        viewHolder.ivFriendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = SocialProfileFragment.newInstance(user);
                manager = ((AppCompatActivity)getActivity())
                        .getSupportFragmentManager();
                FrameLayout fl = getActivity().findViewById(R.id.flPersonalContainer);
                //fl.removeAllViews();
                FragmentTransaction ft = manager
                        .beginTransaction();
                ft.add(R.id.flPersonalContainer, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ProfileFriendViewHolder extends Component.ViewHolder{

        @BindView(R.id.ivFriendPic)
        ImageView ivFriendPic;
        @BindView(R.id.tvFriendName)
        TextView tvFriendName;

        public ProfileFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ProfileFriendViewHolder(view);
        }
    }
}
