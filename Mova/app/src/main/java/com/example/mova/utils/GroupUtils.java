package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class GroupUtils {

    public void queryGroups(User user, AsyncUtils.ListCallback<Group> callback){
        ParseQuery<Group> pqGroups = user.relGroups.getQuery();
        pqGroups.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }
}
