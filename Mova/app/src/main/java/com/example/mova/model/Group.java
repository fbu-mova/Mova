package com.example.mova.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_GOALS = "goals";
    public static final String KEY_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ADMIN = "admin";

    //CreatedAt
    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    //Name
    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    //Description
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    //Goals
    public ParseRelation<Goal> getRelationGoals(){
        //Get the relation of comments
        return getRelation(KEY_GOALS);
    }

    public ParseQuery<Goal> getQueryGoals(){
        //Get the parsequery for comments
        return (ParseQuery<Goal>) (Object) getRelation(KEY_GOALS).getQuery();
    }

    public List<Goal> getListGoals(){
        ParseQuery<Goal> commentsR = getQueryGoals();
        List<Goal> goalList = new ArrayList<Goal>();
        commentsR.findInBackground(new FindCallback<Goal>() {
            @Override
            public void done(List<Goal> objects, ParseException e) {
                if(e != null){
                    Log.e("Post","error retriving post list");
                }
                goalList.addAll(objects);
            }
        });
        return goalList;
    }

    public void addGoal(Goal goal){
        ParseRelation<Goal> goals = getRelationGoals();
        goals.add(goal);
        this.put(KEY_GOALS, goals);
        this.saveInBackground();
    }

    public void removeGoal(Goal goal){
        ParseRelation<Goal> goals = getRelationGoals();
        goals.remove(goal);
        this.put(KEY_GOALS, goals);
        this.saveInBackground();
    }

}
