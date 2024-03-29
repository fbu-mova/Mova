package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseClassName;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

@ParseClassName("Action")
public class Action extends HashableParseObject {

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_TASK = "task";
    public static final String KEY_IS_DONE = "isDone";
    public static final String KEY_IS_CONNECTED_PARENT = "isConnectedToParent";
    public static final String KEY_COMPLETED_AT = "completedAt";
    public static final String KEY_PARENT_GOAL = "parentGoal";
    public static final String KEY_PARENT_SHARED_ACTION = "parentSharedAction";
    public static final String KEY_PARENT_USER = "parentUser";
    public static final String KEY_IS_PRIORITY = "isPriority";

    //Task

    public String getTask(){
        return getString(KEY_TASK);
    }

    public Action setTask(String task){
        put(KEY_TASK, task);
        return this;
    }

    //isDone

    public boolean getIsDone(){
        return getBoolean(KEY_IS_DONE);
    }

    public Action setIsDone(Boolean bool){
        put(KEY_IS_DONE, bool);
        // Set completion time if done
        if (bool) this.setCompletedAt(Calendar.getInstance().getTime());
        else      this.removeCompletedAt();
        return this;
    }

    public Action setDone(){
        put(KEY_IS_DONE, true);
        this.setCompletedAt(Calendar.getInstance().getTime());
        return this;
    }

    // Is Connected to Parent

    public Boolean getIsConnectedToParent(){
        return getBoolean(KEY_IS_CONNECTED_PARENT);
    }

    public Action setIsConnectedToParent(Boolean bool){
        put(KEY_IS_CONNECTED_PARENT, bool);
        return this;
    }

    //Completed At

    //Get date complete add
    public Date getCompletedAt() {
        return getDate(KEY_COMPLETED_AT);
    }

    public Action setCompletedAt(Date date) {
        put(KEY_COMPLETED_AT, date);
        return this;
    }

    public void removeCompletedAt() {
        put(KEY_COMPLETED_AT, JSONObject.NULL);
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

    //IsPriority

    public Boolean getIsPriority() {
        return getBoolean(KEY_IS_PRIORITY);
    }

    public Action setIsPriority(Boolean bool) {
        put(KEY_IS_PRIORITY, bool);
        return this;
    }

    public static class Wrapper {

        public boolean isPriority;
        public String message;

        public Wrapper() {
            this.isPriority = false;
            this.message = "";
        }

        public Wrapper setIsPriority(boolean isPriority) {
            this.isPriority = isPriority;
            return this;
        }

        public Wrapper setMessage(String message) {
            this.message = message;
            return this;
        }

        public boolean getIsPriority() {
            return this.isPriority;
        }

        public String getMessage() {
            return this.message;
        }
    }

}
