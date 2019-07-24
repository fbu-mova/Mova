package com.example.mova.components;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.User;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFriendComponent extends Component{

    private static final String TAG = "ProfileFriendComponent";
    private static final int viewLayoutRes = R.layout.item_profile_friend;

    private User user;
    private View view;
    private ProfileFriendViewHolder viewHolder;
    private Activity activity;

    private ComponentManager componentManager;

    public ProfileFriendComponent(User item){
        super();
        this.user = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new ProfileFriendViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public View getView() {
        return view;
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
    public void render() {
        if(viewHolder == null){
            Log.e(TAG, "Not inflating views in render");
            return;
        }

        viewHolder.tvFriendName.setText(user.getUsername());
        ParseFile file = user.getProfilePic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(activity)
                    .load(imageUrl)
                    .into(viewHolder.ivFriendPic);
        }
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
}
