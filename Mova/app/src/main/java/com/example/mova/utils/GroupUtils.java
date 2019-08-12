package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.example.mova.model.Tag;
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
                    Log.e("GoalUtils", "Error with  user list query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getAdminList(Group group, AsyncUtils.ListCallback<User> callback){
        ParseQuery<User> pqUser = group.relAdmins.getQuery();
        pqUser.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with admin list query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getGroupGoals(Group group, AsyncUtils.ListCallback<Goal> callback){
        ParseQuery<Goal> pqGoals = group.relGoals.getQuery();
        pqGoals.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with goal query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getGroupPosts(Group group, AsyncUtils.ListCallback<Post> callback){
        ParseQuery<Post> pqPosts = group.relPosts.getQuery();
        Post.includeAllPointers(pqPosts);
        pqPosts.orderByDescending(Post.KEY_CREATED_AT);
        pqPosts.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with posts query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getUserGroups(User user, AsyncUtils.ListCallback<Group> callback){
        ParseQuery<Group> pqGroups = user.relGroups.getQuery();
        pqGroups.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> objects, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with  user groups query");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }

    public static void getIsMember(User user, Group group, AsyncUtils.ItemCallback<Boolean> callback){
        getUserGroups(user, (groupList) -> {
            for(Group g: groupList){
                if(g.equals(group)){
                    callback.call(true);
                    return;
                }
            }
            callback.call(false);
        });
    }

    public static void getIsAdmin(User user, Group group, AsyncUtils.ItemCallback<Boolean> callback){
        getAdminList(group, (admins) -> {
            for(User admin: admins){
                if(user.equals(admin)){
                    callback.call(true);
                    return;
                }
            } callback.call(false);
        });
    }

    public static void getTags(Group group, AsyncUtils.ListCallback<Tag> callback){
        ParseQuery<Tag> pqTags = group.relTags.getQuery();
        pqTags.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> objects, ParseException e) {
                if(e != null){
                    Log.e("EventUtils", "Error with query comments");
                    e.printStackTrace();
                    return;
                }
                callback.call(objects);
            }
        });
    }
}
