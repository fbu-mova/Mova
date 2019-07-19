package com.example.mova.model;

import com.example.mova.RelationFrame;
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
    public static final String KEY_CHILD_ACTION = "childAction";
    RelationFrame relationFrame = new RelationFrame(this);

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

    //Users Invovled

    public ParseRelation<User> getRelationUsersInvovled(){
        return getRelation(KEY_USERS_INVOLVED);
    }

    public ParseQuery<User> getQueryUsersInvovled(){
        return relationFrame.getQuery(KEY_USERS_INVOLVED);
    }

    public List<User> getListUsersInvovled(){
        return relationFrame.getList(KEY_USERS_INVOLVED);
    }

    public SharedAction addUserInvovled(User user){
        return (SharedAction) relationFrame.add(KEY_USERS_INVOLVED, user);
    }

    public SharedAction removeUserInvovled(User user){
        return (SharedAction) relationFrame.remove(KEY_USERS_INVOLVED, user);
    }

    //Users done

    public int getUsersDone(){
        return getInt(KEY_USERS_DONE);
    }

    public SharedAction setUsersDone(int done){
        put(KEY_USERS_DONE, done);
        return this;
    }

    //Child action
    public Action getChildAction(){
        return (Action) getParseObject(KEY_CHILD_ACTION);
    }

    public SharedAction setChildAction(Action action){
        put(KEY_CHILD_ACTION, action);
        action.setParentSharedAction(this);
        return this;
    }

    public Action newChildAction(){
        Action action = new Action();
        action.setParentSharedAction(this);
        action.setTask(this.getTask());
        return action;
    }

}
