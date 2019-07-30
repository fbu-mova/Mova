package com.example.mova.fragments.Social;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.EventComposeActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.components.Component;
import com.example.mova.components.EventThumbnailComponent;
import com.example.mova.model.Event;
import com.example.mova.model.User;
import com.example.mova.scrolling.EdgeDecorator;
import com.example.mova.utils.EventUtils;
import com.example.mova.utils.LocationUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    User user;

    @BindView(R.id.svEvents)
    SearchView svEvents;

    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    @BindView(R.id.rvYourEvents) RecyclerView rvYourEvents;
    protected List<Event> yourEvents;
    private DataComponentAdapter<Event> yourEventsAdapter;


    @BindView(R.id.rvNearYou) RecyclerView rvNearYou;
    protected List<Event> nearYouEvents;
    private DataComponentAdapter<Event> nearYouAdapter;



    public EventsFragment() {
        // Required empty public constructor
    }


    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        user = (User) ParseUser.getCurrentUser();
        yourEvents = new ArrayList<>();

        nearYouEvents = new ArrayList<>();

       LocationUtils.saveCurrentUserLocation(getContext());
        ParseGeoPoint userLocation = LocationUtils.getCurrentUserLocation();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventComposeActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        yourEventsAdapter = new DataComponentAdapter<Event>((DelegatedResultActivity) getActivity(),yourEvents) {
            @Override
            public Component makeComponent(Event item) {
                Component component = new EventThumbnailComponent(item);
                return component;
            }
        };



        nearYouAdapter = new DataComponentAdapter<Event>((DelegatedResultActivity) getActivity(), nearYouEvents) {
            @Override
            public Component makeComponent(Event item) {
                Component component = new EventThumbnailComponent(item);
                return component;
            }
        };

        //Toast.makeText(getContext(), userLocation.toString() , Toast.LENGTH_SHORT).show();

        EdgeDecorator decorator = new EdgeDecorator(5);

        rvYourEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNearYou.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvYourEvents.addItemDecoration(decorator);

        rvNearYou.addItemDecoration(decorator);

        rvYourEvents.setAdapter(yourEventsAdapter);

        rvNearYou.setAdapter(nearYouAdapter);

        EventUtils.getYourEvents(user, (yourevents) -> {
            yourEvents.addAll(yourevents);
            yourEventsAdapter.notifyDataSetChanged();
            rvYourEvents.scrollToPosition(0);
        });

        EventUtils.getEventsNearYou(userLocation, (eventsNearYou) -> {
            nearYouEvents.addAll(eventsNearYou);
            nearYouAdapter.notifyDataSetChanged();
            rvNearYou.scrollToPosition(0);
            Log.d("Events Fragment", LocationUtils.getCurrentUserLocation().toString());
        });


    }
}
