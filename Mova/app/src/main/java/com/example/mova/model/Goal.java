package com.example.mova.model;

import com.example.mova.RelationFrame;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

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
    RelationFrame<User> relUsers = new RelationFrame<>(this);
    RelationFrame<SharedAction> relActions = new RelationFrame<>(this);
    RelationFrame<Tag> relTags = new RelationFrame<>(this);

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

    //Users involved

    public ParseRelation<User> getRelationUsersInvolved(){
        return getRelation(KEY_USERS_INVOLVED);
    }

    public ParseQuery<User> getQueryUsersInvolved(){
        return relUsers.getQuery(KEY_USERS_INVOLVED);
    }

    public void getListUsersInvolved(AsyncUtils.ListCallback<User> callback) {
        relUsers.getList(KEY_USERS_INVOLVED, callback);
    }

    public void addUserInvolved(User user, AsyncUtils.ItemCallback<User> callback) {
        relUsers.add(KEY_USERS_INVOLVED, user, callback);
    }

    public User removeUserInvolved(User user, AsyncUtils.EmptyCallback callback) {
        return relUsers.remove(KEY_USERS_INVOLVED, user, callback);
    }

    //Actions

    public ParseRelation<Action> getRelationAction(){
        return getRelation(KEY_ACTIONS);
    }

    public ParseQuery<Action> getQueryAction(){
        return relActions.getQuery(KEY_ACTIONS);
    }

    public void getListActions(AsyncUtils.ListCallback<SharedAction> callback) {
        relActions.getList(KEY_ACTIONS, callback);
    }

    public void addAction(SharedAction action, AsyncUtils.ItemCallback<SharedAction> callback) {
        relActions.add(KEY_ACTIONS, action, callback);
    }

    public SharedAction removeAction(SharedAction action, AsyncUtils.EmptyCallback callback) {
        return relActions.remove(KEY_ACTIONS, action, callback);
    }

    //Tags

    public ParseRelation<Tag> getRelationTags(){
        //Get the relation of tags
        return getRelation(KEY_TAGS);
    }

    public ParseQuery<Tag> getQueryTags(){
        //Get the parsequery for tags
        return relTags.getQuery(KEY_TAGS);
    }

    public void getListTags(AsyncUtils.ListCallback<Tag> callback) {
        relTags.getList(KEY_TAGS, callback);
    }

    public void addTag(Tag tag, AsyncUtils.ItemCallback<Tag> callback) {
        relTags.add(KEY_TAGS, tag, callback);
    }

    public Tag removeTag(Tag tag, AsyncUtils.EmptyCallback callback) {
        return relTags.remove(KEY_TAGS,tag, callback);
    }
}

