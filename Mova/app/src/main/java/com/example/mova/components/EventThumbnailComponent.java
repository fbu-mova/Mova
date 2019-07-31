package com.example.mova.components;

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
import com.example.mova.fragments.Social.EventDetailsFragment;
import com.example.mova.model.Event;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventThumbnailComponent extends Component {

    private static final String TAG = "eventThumbnailComp";
    private static final int viewLayoutRes = R.layout.item_event_thumbnail;

    private Event event;
    private EventThumbnailViewHolder viewHolder;
    public static FragmentManager manager;
    private ComponentManager componentManager;

    public EventThumbnailComponent(Event item){
        super();
        this.event = item;
    }

    @Override
    public ViewHolder getViewHolder() {
        // viewHolder = new EventThumbnailViewHolder(view);
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
        return "EventThumbnail_" + event.getObjectId();
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
//        if (viewHolder == null) {
//            Log.e(TAG, "not inflating views to viewHolder, in render");
//            return;
//        }
        checkViewHolderClass(holder, EventThumbnailViewHolder.class);
        viewHolder = (EventThumbnailViewHolder) holder;

        String location = LocationUtils.makeLocationText(getActivity(),event.getLocation());
        String[] locationsplit = location.split(",");
        String[] state = locationsplit[2].split(" ");

        //viewHolder.tvGroupName.setText();
        viewHolder.tvEventName.setText(event.getTitle());
        viewHolder.tvEventLocation.setText(locationsplit[1] + ", "+ state[1]);
        viewHolder.tvWhen.setText(TimeUtils.toDateString(event.getDate()));
        if(event.getParentGroup() != null){
            event.getParentGroupName(event.getParentGroup(),(name) -> {
                viewHolder.tvGroupName.setText(name);
            });
        }
        ParseFile file = event.getEventPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(viewHolder.ivEventPic);
        }

        viewHolder.ivEventPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = EventDetailsFragment.newInstance(event);
                manager = ((AppCompatActivity)getActivity())
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
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class EventThumbnailViewHolder extends Component.ViewHolder{

        @BindView(R.id.tvGroupName) TextView tvGroupName;
        @BindView(R.id.ivEventPic) ImageView ivEventPic;
        @BindView(R.id.tvEventName) TextView tvEventName;
        @BindView(R.id.tvEventLocation) TextView tvEventLocation;
        @BindView(R.id.tvWhen) TextView tvWhen;

        public EventThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new EventThumbnailViewHolder(view);
        }
    }
}
