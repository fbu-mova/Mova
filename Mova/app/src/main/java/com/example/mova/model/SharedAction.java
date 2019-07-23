package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("SharedAction")
public class SharedAction extends HashableParseObject {

    public static final String KEY_TASK = "task";
    public static final String KEY_GOAL = "goal";
    public static final String KEY_USERS_DONE = "usersDone";

    //Relations
    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_CHILD_ACTIONS = "childActions";
    public final RelationFrame<User> relUsersInvolved = new RelationFrame<>(this, KEY_USERS_INVOLVED);
    public final RelationFrame<Action> relChildActions = new RelationFrame<>(this, KEY_CHILD_ACTIONS);

    //Task
    public String getTask(){
        return getString(KEY_TASK);
    }

    public SharedAction setTask(String task){
        put(KEY_TASK, task);
        return this;
    }

    //Goal
    public Goal getGoal(){
        return (Goal) getParseObject(KEY_GOAL);
    }

    public SharedAction setGoal(Goal goal){
        put(KEY_GOAL, goal);
        return this;
    }


    //Users done

    public int getUsersDone(){
        return getInt(KEY_USERS_DONE);
    }

    public SharedAction setUsersDone(int done){
        put(KEY_USERS_DONE, done);
        return this;
    }
}
