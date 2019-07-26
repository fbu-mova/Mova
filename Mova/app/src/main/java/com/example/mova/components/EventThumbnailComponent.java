package com.example.mova.components;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.model.Event;
import com.parse.ParseFile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventThumbnailComponent extends Component{

    private static final String TAG = "eventThumbnailComp";
    private static final int viewLayoutRes = R.layout.item_event_thumbnail;

    private Event event;
    private View view;
    private EventThumbnailViewHolder viewHolder;
    private DelegatedResultActivity activity;
    public static FragmentManager manager;

    public EventThumbnailComponent(Event item){
        super();
        this.event = item;
    }

    @Override
    public void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot) {
        view = activity.getLayoutInflater().inflate(viewLayoutRes, parent, attachToRoot);
        this.activity = activity;
    }

    @Override
    public ViewHolder getViewHolder() {
        viewHolder = new EventThumbnailViewHolder(view);
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

        //viewHolder.tvGroupName.setText();
        viewHolder.tvEventName.setText(event.getTitle());
        viewHolder.tvEventLocation.setText(event.getLocation().toString());
        viewHolder.tvWhen.setText(event.getDate().toString());

        ParseFile file = event.getEventPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(activity)
                    .load(imageUrl)
                    .into(viewHolder.ivEventPic);
        }


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
}
