package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Event;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class TagUtlis {

    public static void getGroups(Tag tag, AsyncUtils.ListCallback<Group> callback){
        ParseQuery<Group> pqGroups = tag.relGroups.getQuery();
        pqGroups.orderByDescending("members")
        .findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if(e != null){
                    Log.e("TagUtils", "Error with getting groups");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getEvents(Tag tag, AsyncUtils.ListCallback<com.example.mova.model.Event> callback){
        ParseQuery<Event> pqEvent = tag.relEvents.getQuery();
        pqEvent.orderByDescending("members")
                .findInBackground(new FindCallback<Event>() {
                    @Override
                    public void done(List<Event> objects, ParseException e) {
                        if(e != null){
                            Log.e("TagUtils", "Error with getting groups");
                            e.printStackTrace();
                            return;
                        }
                        callback.call(objects);
                    }
                });

    }

    public static void getGoals(Tag tag, AsyncUtils.ListCallback<Goal> callback){
        ParseQuery<Goal> pqGoal = tag.relGoals.getQuery();
        pqGoal.orderByDescending("members")
        .findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                if(e != null){
                    Log.e("TagUtils", "Error with getting groups");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getTags(AsyncUtils.ListCallback<Tag> callback){
        ParseQuery<Tag> pqTag = ParseQuery.getQuery("Tag");
        pqTag.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> objects, ParseException e) {
                if(e != null){
                    Log.e("TagUtils", "Error with getting groups");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }
}
