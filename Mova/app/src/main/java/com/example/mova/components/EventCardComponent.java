package com.example.mova.components;

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
import com.example.mova.fragments.Social.EventDetailsFragment;
import com.example.mova.model.Event;
import com.example.mova.model.Media;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.PostConfig;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventCardComponent extends Component {

    private Event event;
    private ViewHolder holder;
    public static FragmentManager manager;
    private ComponentManager componentManager;

    private boolean allowCompose, allowDetailsClick, showDescription;

    public EventCardComponent(Event event) {
        this.event = event;
        this.allowCompose = true;
        this.allowDetailsClick = true;
        this.showDescription = true;
    }

    public void setAllowCompose(boolean allowCompose) {
        this.allowCompose = allowCompose;
    }

    public void setAllowDetailsClick(boolean allowDetailsClick) {
        this.allowDetailsClick = allowDetailsClick;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }

    @Override
    public ViewHolder getViewHolder() {
        return holder;
    }

    @Override
    public String getName() {
        return "EventCard_" + event.getTitle();
    }

    @Override
    public void setManager(ComponentManager manager) {
        this.componentManager = manager;
    }

    @Override
    public Inflater makeInflater() {
        return new Inflater();
    }

    @Override
    protected void onLaunch() {

    }

    @Override
    protected void onRender(Component.ViewHolder holder) {
        checkViewHolderClass(holder, ViewHolder.class);
        this.holder = (ViewHolder) holder;

        String location = LocationUtils.makeLocationText(getActivity(), event.getLocation(), true);
        if(location != null & !(location.equals(" ")) & !(location.equals("")) ){
            String[] locationsplit = location.split(",");
            int i = locationsplit.length - 2;
            int j = locationsplit.length - 3;
            String[] state = locationsplit[i].split(" ");
            this.holder.tvEventLocation.setText(locationsplit[j] + ", "+ state[1]);
        }

        //viewHolder.tvGroupName.setText();
        this.holder.tvEventName.setText(event.getTitle());

        if (showDescription) {
            this.holder.tvDescription.setVisibility(View.VISIBLE);
            this.holder.tvDescription.setText(event.getDescription());
        } else {
            this.holder.tvDescription.setVisibility(View.GONE);
        }

        this.holder.tvWhen.setText(TimeUtils.toDateString(event.getDate()));
        ParseFile file = event.getEventPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getActivity())
                    .load(imageUrl)
                    .into(this.holder.ivEventPic);
        }

        this.holder.glRoot.setGestureDetector(new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (allowDetailsClick) {
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
                return !allowDetailsClick;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (allowCompose) {
                    PostConfig config = new PostConfig();
                    config.isPersonal = true;
                    config.media = new Media(event);
                    new ComposePostDialog.Builder(getActivity())
                        .setConfig(config)
                        .setOnPost((post) -> {
                            Toast.makeText(getActivity(), "Posted!", Toast.LENGTH_SHORT).show();
                            // TODO: Go to post
                        })
                        .show(EventCardComponent.this.holder.glRoot);
                }
            }
        }));
    }

    @Override
    protected void onDestroy() {

    }

    public static class ViewHolder extends Component.ViewHolder {

        @BindView(R.id.card) protected CardView card;
        @BindView(R.id.glRoot) protected GestureLayout glRoot;
        @BindView(R.id.tvEventName) protected TextView tvEventName;
        @BindView(R.id.tvDescription) protected TextView tvDescription;
        @BindView(R.id.tvWhen) protected TextView tvWhen;
        @BindView(R.id.tvEventLocation) protected TextView tvEventLocation;
        @BindView(R.id.ivEventPic) protected ImageView ivEventPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class Inflater extends Component.Inflater {

        @Override
        public Component.ViewHolder inflate(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.component_event_card, parent, attachToRoot);
            return new ViewHolder(view);
        }
    }
}
