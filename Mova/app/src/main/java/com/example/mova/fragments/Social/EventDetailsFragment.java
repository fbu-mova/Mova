package com.example.mova.fragments.Social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.model.Event;
import com.example.mova.model.Post;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.TimeUtils;
import com.google.android.gms.maps.MapView;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventDetailsFragment extends Fragment {

    Event event;

    @BindView(R.id.mvEventMap)
    MapView mvEventMap;
    @BindView(R.id.tvEventName)
    TextView tvEventName;
    @BindView(R.id.ivEventPic)
    ImageView ivEventPic;
    @BindView(R.id.tvEventLocation)
    TextView tvEventLocation;
    @BindView(R.id.tvEventTime)
    TextView tvEventTime;
    @BindView(R.id.rvEventComments)
    RecyclerView rvEventComments;

    protected List<Post> eventComments;
    private DataComponentAdapter<Post> eventCommentsAdapter;


    public EventDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(Event item) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("event", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        event = this.getArguments().getParcelable("event");
        eventComments = new ArrayList<>();

        String location = LocationUtils.makeLocationText(getContext(),event.getLocation());



        tvEventName.setText(event.getTitle());
        tvEventLocation.setText("Where: " + location);
        tvEventTime.setText("When: " + TimeUtils.toDateString( event.getDate()));

        ParseFile file = event.getEventPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(ivEventPic);
        }
    }
}
