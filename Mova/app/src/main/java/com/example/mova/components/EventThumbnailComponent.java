package com.example.mova.components;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.mova.fragments.Social.EventDetailsFragment;
import com.example.mova.icons.Icons;
import com.example.mova.model.Event;
import com.example.mova.model.Group;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventThumbnailComponent extends Component {

    private static final String TAG = "eventThumbnailComp";
    private static final int viewLayoutRes = R.layout.item_event_thumbnail;

    private Event event;
    private ViewHolder viewHolder;
    public static FragmentManager manager;
    private ComponentManager componentManager;

    public EventThumbnailComponent(Event item){
        super();
        this.event = item;
    }

    @Override
    public Component.ViewHolder getViewHolder() {
        // viewHolder = new ViewHolder(view);
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
    protected void onRender(Component.ViewHolder holder) {
//        if (viewHolder == null) {
//            Log.e(TAG, "not inflating views to viewHolder, in render");
//            return;
//        }
        checkViewHolderClass(holder, ViewHolder.class);
        viewHolder = (ViewHolder) holder;

        String location = LocationUtils.makeLocationText(getActivity(),event.getLocation(), true);
        if(location != null & !(location.equals(" ")) & !(location.equals("")) ){
            String[] locationsplit = location.split(",");
            int i = locationsplit.length - 2;
            int j = locationsplit.length - 3;
//            if(locationsplit.length > 3){
//                i = 3;
//                j = 2;
//            }
//            if(locationsplit.length == 3 ){
//                i = 2;
//                j = 1;
//            }
            String[] state = locationsplit[i].split(" ");
            viewHolder.tvEventLocation.setText(locationsplit[j] + ", "+ state[1]);
        }


        //viewHolder.tvGroupName.setText();
        viewHolder.tvEventName.setText(event.getTitle());

        viewHolder.tvWhen.setText(TimeUtils.toDateString(event.getDate()));
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

        displayGroup();
    }

    private void displayGroup() {
        if (event.getParentGroup() == null) {
            //viewHolder.llGroup.setVisibility(View.GONE);
            viewHolder.cvGroup.setVisibility(View.GONE);
            return;
        }

        event.getParentGroup().fetchIfNeededInBackground((obj, e) -> {
            if (e != null) {
                Log.e("EventThumbnailComponent", "Failed to load event group", e);
                return;
            }

            getActivity().runOnUiThread(() -> {
                Group group = (Group) obj;
                viewHolder.llGroup.setVisibility(View.VISIBLE);
                viewHolder.tvGroup.setText(group.getName());
                Icons.from(getActivity()).displayNounIcon(group, viewHolder.cvGroup, viewHolder.ivGroup);
            });
        });
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.llRoot) public LinearLayout llRoot;

        @BindView(R.id.ivEventPic) ImageView ivEventPic;
        @BindView(R.id.tvEventName) TextView tvEventName;
        @BindView(R.id.tvEventLocation) TextView tvEventLocation;
        @BindView(R.id.tvWhen) TextView tvWhen;

        @BindView(R.id.llGroup) LinearLayout llGroup;
        @BindView(R.id.ivGroup) ImageView ivGroup;
        @BindView(R.id.cvGroup) CardView cvGroup;
        @BindView(R.id.tvGroup) TextView tvGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(viewLayoutRes, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
