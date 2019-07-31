package com.example.mova.components;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.model.Group;
import com.example.mova.utils.GroupUtils;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileShowMoreGroupsComponent extends Component {

    private static final String TAG = "MoreGroupsComponent";
    private static final int viewLayoutRes = R.layout.item_profile_show_more;

    private Group group;
    private ProfileShowMoreGroupsViewHolder viewHolder;

    private ComponentManager componentManager;

    public ProfileShowMoreGroupsComponent(Group item){
        super();
        this.group = item;
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new ProfileShowMoreGroupsViewHolder(view);
        if(viewHolder != null){
            return viewHolder;
        }
        Log.e(TAG, "viewholder not inflating");
        return null;
    }

    @Override
    public String getName() {
        return "ProfileShowMoreGroupsComponent";
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
        checkViewHolderClass(holder, ProfileShowMoreGroupsViewHolder.class);
        viewHolder = (ProfileShowMoreGroupsViewHolder) holder;

//        if(viewHolder == null){
//            Log.e(TAG, "Not inflating views in render");
//            return;
//        }

        viewHolder.tvName.setText(group.getName());
        viewHolder.tvDescription.setText(group.getDescription());
        ParseFile file = group.getGroupPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(viewHolder.ivPic);
        }
        GroupUtils.getUserList(group, (listUsers) -> {
            viewHolder.tvCount.setText(listUsers.size() + " members");
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ProfileShowMoreGroupsViewHolder extends Component.ViewHolder{

        @BindView(R.id.ivPic) ImageView ivPic;
        @BindView(R.id.tvCount) TextView tvCount;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvDescription) TextView tvDescription;

        public ProfileShowMoreGroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ProfileShowMoreGroupsViewHolder(view);
        }
    }
}
