package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class FriendUtils {

    public static void queryFriends(User user, AsyncUtils.ListCallback<User> callback){
        ParseQuery<User> pqFriends = user.relFriends.getQuery();
        pqFriends.findInBackground(new FindCallback<User>() {
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
