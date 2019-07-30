package com.example.mova.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.mova.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

public class LocationUtils {

    private static FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION = 1;
    //private static LocationManager locationManager;

    public static String makeLocationText(Context context, ParseGeoPoint location) {
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addresses.get(0);
            String cityName = address.getAddressLine(0);
//            String stateName = address.getAddressLine(1);
//            String countryName = address.getAddressLine(2);
            return cityName;
        } catch (Exception e) {
            Log.e("LocationUtils", "Bad Geocoder conversion from location");
            e.printStackTrace();
            return "";
        }
    }

    public static ParseGeoPoint toGeoPoint(Location location) {
        ParseGeoPoint point = new ParseGeoPoint();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        return point;
    }


    public static void saveCurrentUserLocation(Context context) {
        // requesting permission to get user's location
        if(ActivityCompat.checkSelfPermission((context), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity)context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {
//            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//            // getting last know user's location
//            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // checking if the location is null
                            if(location != null){
                                // if it isn't, save it to Back4App Dashboard
                                ParseGeoPoint currentUserLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                                ParseUser currentUser = ParseUser.getCurrentUser();

                                if (currentUser != null) {
                                    currentUser.put("Location", currentUserLocation);
                                    currentUser.saveInBackground();
                                } else {
                                    // do something like coming back to the login activity
                                }
                            }
                            else {
                                Log.e("LocationUtils", "Location is null");
                            }
                        }
                    });


        }
    }


    public static ParseGeoPoint getCurrentUserLocation(){

        // finding currentUser
        User currentUser = User.getCurrentUser();

        if (currentUser == null) {
            // if it's not possible to find the user, do something like returning to login activity
            return null;
        }
        // otherwise, return the current user location
        return currentUser.getLocation();

    }
}
