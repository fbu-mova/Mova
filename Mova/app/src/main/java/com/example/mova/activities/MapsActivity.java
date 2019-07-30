package com.example.mova.activities;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.mova.R;
import com.example.mova.model.User;
import com.example.mova.utils.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    private GoogleMap mMap;
    private User user;
    Context context;

    public MapsActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        context = getApplicationContext();
        user = (User) ParseUser.getCurrentUser();
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        showCurrentUserInMap(googleMap);
    }





    private void showCurrentUserInMap(final GoogleMap googleMap){

        // calling retrieve user's location method of Step 4
        ParseGeoPoint currentUserLocation = LocationUtils.getCurrentUserLocation();

        // creating a marker in the map showing the current user location
        LatLng currentUser = new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(currentUser).title(ParseUser.getCurrentUser().getUsername()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // zoom the map to the currentUserLocation
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUser, 5));
    }

}
