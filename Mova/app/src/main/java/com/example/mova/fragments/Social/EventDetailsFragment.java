package com.example.mova.fragments.Social;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.activities.EventComposeActivity;
import com.example.mova.adapters.DataComponentAdapter;
import com.example.mova.component.Component;
import com.example.mova.components.PostComponent;
import com.example.mova.model.Event;
import com.example.mova.model.Post;
import com.example.mova.model.User;
import com.example.mova.utils.EventUtils;
import com.example.mova.utils.LocationUtils;
import com.example.mova.utils.TimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EventDetailsFragment extends Fragment implements OnMapReadyCallback {

    Event event;
    User user;

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
    @BindView(R.id.tvDescription)
    TextView tvDescription;
    @BindView(R.id.rvEventComments)
    RecyclerView rvEventComments;
    @BindView(R.id.btnEventAction)
    Button btnEventAction;

    protected List<Post> eventComments;
    private DataComponentAdapter<Post> eventCommentsAdapter;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


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

        return inflater.inflate(R.layout.fragment_event_details, container, false);

    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mvEventMap.onCreate(mapViewBundle);

        mvEventMap.getMapAsync(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        initGoogleMap(savedInstanceState);

        event = this.getArguments().getParcelable("event");
        user = (User) ParseUser.getCurrentUser();
        eventComments = new ArrayList<>();

        String location = LocationUtils.makeLocationText(getContext(),event.getLocation(), false);



        tvEventName.setText(event.getTitle());
        tvEventLocation.setText("Where: " + location);
        tvEventTime.setText("When: " + TimeUtils.toDateString( event.getDate()));
        tvDescription.setText(event.getDescription());

        ParseFile file = event.getEventPic();
        if(file != null){
            String imageUrl = file.getUrl();
            Glide.with(getContext())
                    .load(imageUrl)
                    .into(ivEventPic);
        }

        eventCommentsAdapter = new DataComponentAdapter<Post>((DelegatedResultActivity) getActivity(), eventComments) {
            @Override
            public Component makeComponent(Post item, Component.ViewHolder holder) {
                Component component = new PostComponent(item);
                return component;
            }

            @Override
            protected Component.Inflater makeInflater(Post item) {
                return new PostComponent.Inflater();
            }
        };

        rvEventComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEventComments.setAdapter(eventCommentsAdapter);


        EventUtils.getEventComments(event, (comments) -> {
            eventComments.addAll(comments);
            eventCommentsAdapter.notifyDataSetChanged();
            rvEventComments.scrollToPosition(0);
        });

        //If host display edit, else either join or leave
        if(event.isHostUser(user)){
            btnEventAction.setText("EDIT EVENT");
            btnEventAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EventComposeActivity.class);
                    intent.putExtra("event", event);
                    startActivity(intent);
                }
            });
        }else{
            EventUtils.isInvolved(user,event, (bool) -> {
               if(bool){
                   btnEventAction.setText("LEAVE EVENT");
               }else{
                   btnEventAction.setText("JOIN EVENT");
               }

               btnEventAction.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if(bool){
                           event.relParticipants.remove(user, () -> {
                               btnEventAction.setText("JOIN EVENT");
                               user.relEvents.remove(event, () -> {});
                           });
                       }else{
                           event.relParticipants.add(user);
                           btnEventAction.setText("LEAVE EVENT");
                           user.relEvents.add(event);
                       }
                   }
               });
            });
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mvEventMap.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mvEventMap.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mvEventMap.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mvEventMap.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        float zoomLevel = 14.0f;
        if(event.getLocation() != null) {
            LatLng latLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());
            map.addMarker(new MarkerOptions().position(latLng).title(event.getLocation().toString()));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
            map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                //Dont allow the camera to move
                @Override
                public void onCameraMove() {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                }
            });
        }
    }

    @Override
    public void onPause() {
        mvEventMap.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mvEventMap.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mvEventMap.onLowMemory();
    }
}
