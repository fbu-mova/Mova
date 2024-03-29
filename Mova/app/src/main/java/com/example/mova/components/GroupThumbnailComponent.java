package com.example.mova.components;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.component.ComponentManager;
import com.example.mova.containers.GestureLayout;
import com.example.mova.dialogs.ComposePostDialog;
import com.example.mova.fragments.Social.GroupDetailsFragment;
import com.example.mova.icons.Icons;
import com.example.mova.model.Group;
import com.example.mova.model.Media;
import com.example.mova.utils.PostConfig;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupThumbnailComponent extends Component {

    private static final String TAG = "group thumbnail comp'nt";
    private static final int viewLayoutRes = R.layout.item_group_thumbnail;

    private Group group;
    private GroupThumbnailViewHolder viewHolder;
    public static FragmentManager manager;
    private ComponentManager componentManager;

    private boolean allowDetailsClick = true;
    private boolean allowCompose = true;

    public GroupThumbnailComponent(Group item){
        super();
        this.group = item;
    }

    public void setAllowDetailsClick(boolean allowDetailsClick) {
        this.allowDetailsClick = allowDetailsClick;
    }

    public void setAllowCompose(boolean allowCompose) {
        this.allowCompose = allowCompose;
    }

    @Override
    public ViewHolder getViewHolder() {
//        viewHolder = new GroupThumbnailViewHolder(view);
        if (viewHolder != null) {
            return viewHolder;
        }
        Log.e(TAG, "not inflating views to viewHolder, in getViewHolder");
        return null;
    }

    @Override
    public Component.Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.componentManager = manager;
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(ViewHolder holder) {
        checkViewHolderClass(holder, GroupThumbnailViewHolder.class);
        viewHolder = (GroupThumbnailViewHolder) holder;

//        if (viewHolder == null) {
//            Log.e(TAG, "not inflating views to viewHolder, in render");
//            return;
//        }

        viewHolder.tvGroupName.setText(group.getName());
        ParseFile file = group.getGroupPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getActivity())
                    .load(imageUrl)
                    //.transform(new CenterCrop(), new RoundedCorners(150))
                    .error(R.color.colorAccent) // todo - replace to be better image, add rounded corners
                    .placeholder(R.color.colorAccent)
                    .into(viewHolder.ivGroupPic);
        }

//        group.getNounIcon((icon, e) -> {
//            getActivity().runOnUiThread(() -> {
//                if(icon != null){
//                    Icons.displayNounIcon(group, viewHolder.cvGroupIcon, viewHolder.ivGroupIcon);
//                }
//            });
//        });
        getActivity().runOnUiThread(() -> {
            Icons.from(getActivity()).displayNounIcon(group, viewHolder.cvGroupIcon, viewHolder.ivGroupIcon);
        });

//        viewHolder.ivGroupPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(getActivity().equals(SearchActivity.class)){
////
////                }
//                if (allowDetailsClick) {
//                    Fragment frag = GroupDetailsFragment.newInstance(group);
//                    manager = ((AppCompatActivity) getActivity())
//                            .getSupportFragmentManager();
//                    FrameLayout fl = getActivity().findViewById(R.id.flSocialContainer);
//                    //fl.removeAllViews();
//                    FragmentTransaction ft = manager
//                            .beginTransaction();
//                    ft.add(R.id.flSocialContainer, frag);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    ft.addToBackStack(null);
//                    ft.commit();
//                }
//            }
//        });

        viewHolder.glRoot.setGestureDetector(new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (allowDetailsClick) {
                    Fragment frag = GroupDetailsFragment.newInstance(group);
                    manager = ((AppCompatActivity) getActivity())
                            .getSupportFragmentManager();
                    FrameLayout fl = getActivity().findViewById(R.id.flSocialContainer);
                    //fl.removeAllViews();
                    FragmentTransaction ft = manager
                            .beginTransaction();
                    ft.add(R.id.flSocialContainer, frag);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                return !allowDetailsClick;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (allowCompose) {
                    PostConfig config = new PostConfig();
                    config.isPersonal = true;
                    config.media = new Media(group);

                    new ComposePostDialog.Builder(getActivity())
                            .setConfig(config)
                            .setOnPost((post) -> {
                                Toast.makeText(getActivity(), "Posted!", Toast.LENGTH_SHORT).show();
                                // TODO: Go to post
                            })
                            .show(viewHolder.glRoot);
                }
            }
        }));
    }

    @Override
    protected void onDestroy() {

    }

    public static class GroupThumbnailViewHolder extends Component.ViewHolder{

        @BindView(R.id.glRoot) protected GestureLayout glRoot;
        @BindView(R.id.tvGroupName) protected TextView tvGroupName;
        @BindView(R.id.ivGroupPic) protected ImageView ivGroupPic;
        @BindView(R.id.ivGroupIcon) public ImageView ivGroupIcon;
        @BindView(R.id.cvGroupIcon) public CardView cvGroupIcon;

        public GroupThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new GroupThumbnailViewHolder(view);
        }
    }
}
