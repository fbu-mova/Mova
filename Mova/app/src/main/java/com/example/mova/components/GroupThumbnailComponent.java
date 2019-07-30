package com.example.mova.components;

import android.util.Log;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.fragments.Social.GroupDetailsFragment;
import com.example.mova.model.Group;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupThumbnailComponent extends Component {

    private static final String TAG = "group thumbnail comp'nt";
    private static final int viewLayoutRes = R.layout.item_group_thumbnail;

    private Group group;
    private View view;
    private GroupThumbnailViewHolder viewHolder;
    private DelegatedResultActivity activity;
    public static FragmentManager manager;

    public GroupThumbnailComponent(Group item){
        super();
        this.group = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new GroupThumbnailViewHolder(view);
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setManager(ComponentManager manager) {

    }

    @Override
    public void render() {
        if (viewHolder == null) {
            Log.e(TAG, "not inflating views to viewHolder, in render");
            return;
        }

        viewHolder.tvGroupName.setText(group.getName());
        ParseFile file = group.getGroupPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(activity)
                    .load(imageUrl)
                    .transform(new CenterCrop(), new RoundedCorners(150))
                    .error(R.color.colorAccent) // todo - replace to be better image, add rounded corners
                    .placeholder(R.color.colorAccent)
                    .into(viewHolder.ivGroupPic);
        }

        viewHolder.ivGroupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = GroupDetailsFragment.newInstance(group);
                manager = ((AppCompatActivity)activity)
                        .getSupportFragmentManager();
                FrameLayout fl = activity.findViewById(R.id.flSocialContainer);
                //fl.removeAllViews();
                FragmentTransaction ft = manager
                        .beginTransaction();
                ft.add(R.id.flSocialContainer, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    public static class GroupThumbnailViewHolder extends Component.ViewHolder{

        @BindView(R.id.tvGroupName) protected TextView tvGroupName;
        @BindView(R.id.ivGroupPic) protected ImageView ivGroupPic;

        public GroupThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
