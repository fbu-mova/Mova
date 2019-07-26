package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Event;
import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.List;

public class EventUtils {

    public static void getYourEvents(User user, AsyncUtils.ListCallback<Event> callback){
        ParseQuery<Event> pqYourEvents = user.relEvents.getQuery();
        pqYourEvents.orderByDescending("date");
        pqYourEvents.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e != null){
                    Log.e("EventUtils", "Error with query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getGroupEvents(Group group, AsyncUtils.ListCallback<Event> callback){
        ParseQuery<Event> pqGroupEvents = group.relEvents.getQuery();
        pqGroupEvents.orderByDescending("date");
        pqGroupEvents.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e != null){
                    Log.e("EventUtils", "Error with query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getEventsNearYou(ParseGeoPoint location, AsyncUtils.ListCallback<Event> callback){
        ParseQuery<Event> pqNearYou = ParseQuery.getQuery("Event");
        pqNearYou.whereNear("location", location);
        pqNearYou.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, ParseException e) {
                if(e != null){
                    Log.e("EventUtils", "Error with query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }
}
