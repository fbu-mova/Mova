package com.example.mova.fragments.Social;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.EventComposeActivity;
import com.example.mova.activities.SearchActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.EventThumbnailComponent;
import com.example.mova.containers.EdgeDecorator;
import com.example.mova.fragments.SearchFragment;
import com.example.mova.model.Event;
import com.example.mova.model.User;
import com.example.mova.utils.EventUtils;
import com.example.mova.utils.LocationUtils;
import com.example.mova.views.EdgeFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseGeoPoint;

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
    public static FragmentManager manager;

    @BindView(R.id.ibSearch)
    ImageButton ibSearch;

    @BindView(R.id.efabCompose)
    EdgeFloatingActionButton fabAdd;

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
        user = User.getCurrentUser();
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

        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), ibSearch ,"search");
//                startActivity(intent, options.toBundle());

                Fragment frag = new SearchFragment();
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

        yourEventsAdapter = new DataComponentAdapter<Event>((DelegatedResultActivity) getActivity(),yourEvents) {
            @Override
            public Component makeComponent(Event item, Component.ViewHolder holder) {
                Component component = new EventThumbnailComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Event item) {
                return new EventThumbnailComponent.Inflater();
            }
        };



        nearYouAdapter = new DataComponentAdapter<Event>((DelegatedResultActivity) getActivity(), nearYouEvents) {
            @Override
            public Component makeComponent(Event item, Component.ViewHolder holder) {
                Component component = new EventThumbnailComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Event item) {
                return new EventThumbnailComponent.Inflater();
            }
        };

        //Toast.makeText(getContext(), userLocation.toString() , Toast.LENGTH_SHORT).show();

        Resources resources = getResources();
        EdgeDecorator decorator = new EdgeDecorator((int) resources.getDimension(R.dimen.innerMargin), EdgeDecorator.Orientation.Horizontal);

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
