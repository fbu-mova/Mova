package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseClassName;

import java.util.Calendar;
import java.util.Date;

@ParseClassName("Action")
public class Action extends HashableParseObject {

    public static final String KEY_TASK = "task";
    public static final String KEY_IS_DONE = "isDone";
    public static final String KEY_IS_CONNECTED_PARENT = "isConnectedToParent";
    public static final String KEY_COMPLETED_AT = "completedAt";
    public static final String KEY_PARENT_GOAL = "parentGoal";
    public static final String KEY_PARENT_SHARED_ACTION = "parentSharedAction";
    public static final String KEY_PARENT_USER = "parentUser";

    //Task

    public String getTask(){
        return getString(KEY_TASK);
    }

    public Action setTask(String task){
        put(KEY_TASK, task);
        return this;
    }

    //isDone

    public Boolean getIsDone(){
        return getBoolean(KEY_IS_DONE);
    }

    public Action setIsDone(Boolean bool){
        put(KEY_IS_DONE, bool);
        return this;
    }

    public Action setDone(){
        put(KEY_IS_DONE, true);
        this.setCompletedAt(Calendar.getInstance().getTime());
        return this;
    }

    // Is Connected to Parent

    private Boolean getIsConnectedToParent(){
        return getBoolean(KEY_IS_CONNECTED_PARENT);
    }

    private Action setIsConnectedToParent(Boolean bool){
        put(KEY_IS_CONNECTED_PARENT, bool);
        return this;
    }

    //Completed At

    //Get date complete add
    public Date getCompletedAt(){
        Date date = getDate(KEY_COMPLETED_AT);
        return TimeUtils.normalizeToDay(date);
    }

    public Action setCompletedAt(Date date){
        put(KEY_COMPLETED_AT, date);
        return this;
    }



    //Parent Goal
    public Goal getParentGoal(){
        return (Goal) getParseObject(KEY_PARENT_GOAL);
    }

    public Action setParentGoal(Goal goal){
        put(KEY_PARENT_GOAL, goal);
        return this;
    }

    //Parent Shared Action

    public SharedAction getParentSharedAction(){
        return (SharedAction) getParseObject(KEY_PARENT_SHARED_ACTION);
    }


    public Action setParentSharedAction(SharedAction sharedAction){
        put(KEY_PARENT_SHARED_ACTION, sharedAction);
        return this;
    }

    public Action setParentSharedAction(SharedAction sharedAction, AsyncUtils.ItemCallback<Action> callback){
        // does what updateWithRelation does but for this specific case

        put(KEY_PARENT_SHARED_ACTION, sharedAction);
        sharedAction.relChildActions.add(this, callback);
        return this;
    }

    //Parent User
    public User getParentUser(){
        return  (User) getParseUser(KEY_PARENT_USER);
    }

    public Action setParentUser(User user){
        put(KEY_PARENT_USER, user);
        return this;
    }


}
