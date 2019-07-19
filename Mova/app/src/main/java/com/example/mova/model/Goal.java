package com.example.mova.model;

import android.util.Log;

import com.example.mova.RelationFrame;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Goal")
public class Goal extends ParseObject {

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_COLOR = "color";
    RelationFrame relationFrame = new RelationFrame(this);
    List<SharedAction> saList = new ArrayList<>();

    //Author

    public User getAuthor(){
        return (User) getParseUser(KEY_AUTHOR);
    }

    public Goal setAuthor(User user){
        put(KEY_AUTHOR, user);
        return this;
    }

    //Color
    public String getColor(){
        return getString(KEY_COLOR);
    }

    public Goal setColor(String color){
        put(KEY_COLOR, color);
        return this;
    }

    //Title

    public String getTitle(){
        return getString(KEY_TITLE);
    }

    public Goal setTitle(String title){
        put(KEY_TITLE,title);
        return this;
    }

    //Description

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public Goal setDescription(String description){
        put(KEY_DESCRIPTION,description);
        return this;
    }

    //Users invovled

    public ParseRelation<User> getRelationUsersInvovled(){
        return getRelation(KEY_USERS_INVOLVED);
    }

    public ParseQuery<User> getQueryUsersInvovled(){
        return relationFrame.getQuery(KEY_USERS_INVOLVED);
    }

    public List<User> getListUsersInvovled(){
        return relationFrame.getList(KEY_USERS_INVOLVED);
    }

    public Goal addUserInvovled(User user){
        return (Goal) relationFrame.add(KEY_USERS_INVOLVED, user);
    }

    public Goal removeUserInvovled(User user){
        return (Goal) relationFrame.remove(KEY_USERS_INVOLVED, user);
    }

    //Actions

    public ParseRelation<SharedAction> getRelationAction(){
        return getRelation(KEY_ACTIONS);
    }

    public ParseQuery<SharedAction> getQueryAction(){
        return relationFrame.getQuery(KEY_ACTIONS);
    }

    public List<SharedAction> getListAction(){
        return relationFrame.getList(KEY_ACTIONS);
    }

    public Goal addAction(SharedAction action){
        return (Goal) relationFrame.add(KEY_ACTIONS, action);
    }

    private Goal removeAction(SharedAction action){
        return (Goal) relationFrame.remove(KEY_ACTIONS, action);
    }

    public void setSharedActionList(AsyncUtils.EmptyCallback callback){

        ParseRelation<SharedAction> relation = this.getRelation(KEY_ACTIONS);
        ParseQuery<SharedAction> pqSharedAction = relation.getQuery();
        pqSharedAction.include(SharedAction.KEY_CHILD_ACTION);
        pqSharedAction.findInBackground(new FindCallback<SharedAction>() {
            @Override
            public void done(List<SharedAction> objects, ParseException e) {
                if(e != null){
                    Log.e("Goal", "Error with query");
                    e.printStackTrace();
                    return;
                }else{
                    saList.addAll(objects);
                    callback.call();
                }
            }
        });
    }

    private int getActionsCompleted(Date date, List<SharedAction> saList) {
        List<Action> aList = new ArrayList<>();
        for(int i = 0; i < saList.size(); i++){
            Action action = saList.get(i).getChildAction();
            if(action.getCompletedAt().equals((date))){
                aList.add(action);
            }
        }
        return aList.size();
    }


    //Todo - get data to display
    public void createGraph(int length, AsyncUtils.ItemCallback<LineGraphSeries> callback){
        DataPoint[] points = new DataPoint[length];
        AsyncUtils.executeMany(length,
                (position, callback2) -> {
                    Date date = TimeUtils.getToday();
                    final int index = position;
                    //Todo set date equal to current date minus j
                    setSharedActionList(() -> {
                        DataPoint point = new DataPoint(index,
                                getActionsCompleted(TimeUtils.normalizeToDay(date), saList));
                        points[index] = point;
                        callback2.call(null);
                    });
                },
                () -> {
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
                    callback.call(series);
                });
    }


    //Tags

    public ParseRelation<Tag> getRelationTags(){
        //Get the relation of tags
        return getRelation(KEY_TAGS);
    }

    public ParseQuery<Tag> getQueryTags(){
        //Get the parsequery for tags
        return relationFrame.getQuery(KEY_TAGS);
    }

    public List<Tag> getListTags(){
        return relationFrame.getList(KEY_TAGS);
    }

    public Goal addTag(Tag tag){
        return (Goal) relationFrame.add(KEY_TAGS, tag);
    }

    public Goal removeTag(Tag tag){
        return (Goal) relationFrame.remove(KEY_TAGS,tag);
    }
}

