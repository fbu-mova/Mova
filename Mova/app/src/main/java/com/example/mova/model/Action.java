package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Action")
public class Action extends ParseObject {

    public static final String KEY_TASK = "task";
    public static final String KEY_PARENT_SHARED_ACTION = "parentSharedAction";
    public static final String KEY_IS_DONE = "isDone";
    public static final String KEY_IS_CONNECTED_PARENT = "isConnectedToParent";

    //Task

    public String getTask(){
        return getString(KEY_TASK);
    }

    public Action setTask(String task){
        put(KEY_TASK, task);
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

    //isDone

    public Boolean getIsDone(){
        return getBoolean(KEY_IS_DONE);
    }

    public Action setIsDone(Boolean bool){
        put(KEY_IS_DONE, bool);
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

}
