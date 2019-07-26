package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Group;
import com.example.mova.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class GroupUtils {

    /**
     * Finds the groups that a user is in.
     * @param user The user in question.
     * @param callback The callback that lets us do what we want with the list of Groups we receive.
     */
    public static void queryGroups(User user, AsyncUtils.ListCallback<Group> callback){
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

    public static void getUserList(Group group, AsyncUtils.ListCallback<User> callback){
        ParseQuery<User> pqUser = group.relUsers.getQuery();
        pqUser.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
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
