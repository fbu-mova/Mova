package com.example.mova.components;

import android.app.Activity;
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
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.fragments.Social.GroupDetailsFragment;
import com.example.mova.model.Group;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileGroupComponent extends Component {

    private static final String TAG = "ProfileGroupComponent";
    private static final int viewLayoutRes = R.layout.item_profile_group;

    private Group group;
    private View view;
    private ProfileGroupViewHolder viewHolder;
    private Activity activity;
    public static FragmentManager manager;

    private ComponentManager componentManager;

    public ProfileGroupComponent(Group item){
        super();
        this.group = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, false);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new ProfileGroupViewHolder(view);
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
        return "ProfileGroupComponent";
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

        viewHolder.tvName.setText(group.getName());
        ParseFile file = group.getGroupPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(activity)
                    .load(imageUrl)
                    .into(viewHolder.ivGroupPic);
        }

        viewHolder.ivGroupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = GroupDetailsFragment.newInstance(group);
                manager = ((AppCompatActivity)activity)
                        .getSupportFragmentManager();
                FrameLayout fl = activity.findViewById(R.id.flPersonalContainer);
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

    public static class ProfileGroupViewHolder extends Component.ViewHolder{

        @BindView(R.id.ivGroupPic)
        ImageView ivGroupPic;
        @BindView(R.id.tvName)
        TextView tvName;

        public ProfileGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
