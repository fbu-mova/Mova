package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("SharedAction")
public class SharedAction extends HashableParseObject {

    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_TASK = "task";
    public static final String KEY_GOAL = "goal";
    public static final String KEY_USERS_DONE = "usersDone";
    public static final String KEY_IS_PRIORITY = "isPriority";

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

    public int getUsersDone() {
        return getInt(KEY_USERS_DONE);
    }

    public SharedAction setUsersDone(int done){
        put(KEY_USERS_DONE, done);
        return this;
    }

    //IsPriority

    public Boolean getIsPriority() {
        return getBoolean(KEY_IS_PRIORITY);
    }

    public SharedAction setIsPriority(Boolean bool) {
        put(KEY_IS_PRIORITY, bool);
        return this;
    }

    public static class Data {
        public SharedAction sharedAction;
        public boolean isUserDone;

        public Data(SharedAction sharedAction, boolean isUserDone) {
            this.sharedAction = sharedAction;
            this.isUserDone = isUserDone;
        }
    }
}
