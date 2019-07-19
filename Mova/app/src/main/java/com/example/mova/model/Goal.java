package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import static com.example.mova.model.Action.KEY_PARENT_USER;

@ParseClassName("Goal")
public class Goal extends ParseObject {

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_SHARED_ACTIONS = "sharedActions";
    public static final String KEY_TAGS = "tags"; // optional
    public static final String KEY_COLOR = "color";
    public static final String KEY_FROM_GROUP = "fromGroup"; // optional
    public static final String KEY_IMAGE = "image"; // optional

    RelationFrame<User> relUsers = new RelationFrame<>(this, KEY_USERS_INVOLVED);
    RelationFrame<Action> relActions = new RelationFrame<>(this, KEY_ACTIONS);
    RelationFrame<SharedAction> relSharedActions = new RelationFrame<>(this, KEY_SHARED_ACTIONS);
    RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);

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

    //fromGroup: returns the group name (if it exists) to the goal. else, returns null

    public Group getFromGroup() {
        return (Group) get(KEY_FROM_GROUP);
    }

    public String getFromGroupName() {
        Group group = getFromGroup();
        if (group == null) {
            return "";
        }

        return group.getName();
    }

    //Image

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public Goal setImage(ParseFile file) {
        put(KEY_IMAGE, file);
        return this;
    }

    //Users involved

    public ParseRelation<User> getRelationUsersInvolved(){
        return getRelation(KEY_USERS_INVOLVED);
    }

    public ParseQuery<User> getQueryUsersInvolved(){
        return relUsers.getQuery();
    }

    public void getListUsersInvolved(AsyncUtils.ListCallback<User> callback) {
        relUsers.getList(callback);
    }

    public void addUserInvolved(User user, AsyncUtils.ItemCallback<User> callback) {
        relUsers.add(user, callback);
    }

    public User removeUserInvolved(User user, AsyncUtils.EmptyCallback callback) {
        return relUsers.remove(user, callback);
    }

    //Shared Actions

    public ParseRelation<SharedAction> getRelationSharedAction(){
        return getRelation(KEY_SHARED_ACTIONS);
    }

    public ParseQuery<SharedAction> getQuerySharedAction(){
        return relSharedActions.getQuery();
    }

    public void getListSharedActions(AsyncUtils.ListCallback<SharedAction> callback) {
        relSharedActions.getList(callback);
    }

    public void addSharedAction(SharedAction action, AsyncUtils.ItemCallback<SharedAction> callback) {
        relSharedActions.add(action, callback);
    }

    public SharedAction removeSharedAction(SharedAction action, AsyncUtils.EmptyCallback callback) {
        return relSharedActions.remove(action, callback);
    }

    //Actions
    public ParseRelation<Action> getRelationAction() {
        return getRelation(KEY_ACTIONS);
    }

    public ParseQuery<Action> getQueryAction() {
        return relActions.getQuery();
    }

    // returns all the actions of a goal that the user has personally
    public ParseQuery<Action> ofCurrentUser() {
        return getQueryAction().whereEqualTo(KEY_PARENT_USER, ParseUser.getCurrentUser());
    }


    //Tags

    public ParseRelation<Tag> getRelationTags(){
        //Get the relation of tags
        return getRelation(KEY_TAGS);
    }

    public ParseQuery<Tag> getQueryTags(){
        //Get the parsequery for tags
        return relTags.getQuery();
    }

    public void getListTags(AsyncUtils.ListCallback<Tag> callback) {
        relTags.getList(callback);
    }

    public void addTag(Tag tag, AsyncUtils.ItemCallback<Tag> callback) {
        relTags.add(tag, callback);
    }

    public Tag removeTag(Tag tag, AsyncUtils.EmptyCallback callback) {
        return relTags.remove(tag, callback);
    }

    public void addAction(Action action) {
    }

    public static class Query extends ParseQuery<Goal> {

        public Query() {
            super(Goal.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withGroup() {
            include(KEY_FROM_GROUP);
            return this;
        }
    }
}

