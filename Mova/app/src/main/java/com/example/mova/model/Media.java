package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Media")
public class Media extends ParseObject {

    public static final String KEY_TYPE = "type";
    public static final String KEY_PARENT = "parent";
    public static final String KEY_POST = "contentPost";
    public static final String KEY_GROUP = "contentGroup";
    public static final String KEY_EVENT = "contentEvent";
    public static final String KEY_GOAL = "contentGoal";
    public static final String KEY_ACTION = "contentAction";

    //Type
    public int getType(){
        return getInt(KEY_TYPE);
    }

    public Media setType(int i){
        put(KEY_TYPE, i);
        return this;
    }

    //Parent
    public User getParent(){
        return (User) getParseUser(KEY_PARENT);
    }

    public Media setParent(User user){
        put(KEY_PARENT, user);
        return this;
    }

    //ContentPost
    public Post getContentPost(){
        return (Post) getParseObject(KEY_POST);
    }

    public void setContentPost(Post post){
        put(KEY_POST, post);
    }

    //ContentGroup
    public Group getContentGroup(){
        return (Group) getParseObject(KEY_GROUP);
    }

    public void setContentGroup(Group group){
        put(KEY_GROUP,group);
    }

    //ContentEvent
    public Event getContentEvent(){
        return (Event) getParseObject(KEY_EVENT);
    }

    public void setContentEvent(Event event){
        put(KEY_EVENT, event);
    }

    //ContentGoal
    public Goal getContentGoal(){
        return (Goal) getParseObject(KEY_GOAL);
    }

    public void setContentGoal(Goal goal){
        put(KEY_GOAL, goal);
    }

    //ContentAction
    public Action getContentAction(){
        return (Action) getParseObject(KEY_ACTION);
    }

    public void setContentAction(Action action){
        put(KEY_ACTION,action);
    }
}
