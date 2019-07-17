package com.example.mova;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.List;

public class LocationUtils {
    public static String makeLocationText(Context context, ParseGeoPoint location) {
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = addresses.get(0);
            String cityName = address.getAddressLine(0);
            String stateName = address.getAddressLine(1);
            String countryName = address.getAddressLine(2);
            return cityName + ", " + stateName + ", " + countryName;
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
}
