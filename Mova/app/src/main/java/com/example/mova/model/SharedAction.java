package com.example.mova.model;

import com.example.mova.RelationFrame;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

@ParseClassName("SharedAction")
public class SharedAction extends ParseObject {

    public static final String KEY_TASK = "task";
    public static final String KEY_GOAL = "goal";
    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_USERS_DONE = "usersDone";
    RelationFrame<User> relUsersInvolved = new RelationFrame<>(this);

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

    //Users Involved

    public ParseRelation<User> getRelationUsersInvovled(){
        return getRelation(KEY_USERS_INVOLVED);
    }

    public ParseQuery<User> getQueryUsersInvovled(){
        return relUsersInvolved.getQuery(KEY_USERS_INVOLVED);
    }

    public void getListUsersInvolved(AsyncUtils.ListCallback<User> callback) {
        relUsersInvolved.getList(KEY_USERS_INVOLVED, callback);
    }

    public void addUserInvolved(User user, AsyncUtils.ItemCallback<User> callback) {
        relUsersInvolved.add(KEY_USERS_INVOLVED, user, callback);
    }

    public User removeUserInvolved(User user, AsyncUtils.EmptyCallback callback) {
        return relUsersInvolved.remove(KEY_USERS_INVOLVED, user, callback);
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
