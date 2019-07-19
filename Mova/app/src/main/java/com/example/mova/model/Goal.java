package com.example.mova.model;

import com.example.mova.RelationFrame;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

import static com.example.mova.model.Group.KEY_NAME;

@ParseClassName("Goal")
public class Goal extends ParseObject {

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_COLOR = "color";
    public static final String KEY_FROM_GROUP = "fromGroup";
    public static final String KEY_IMAGE = "image";
    RelationFrame relationFrame = new RelationFrame(this);



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

    //Image

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public Goal setImage(ParseFile file) {
        put(KEY_IMAGE, file);
        return this;
    }

    //Users involved

    public ParseRelation<User> getRelationUsersInvovled(){
        return getRelation(KEY_USERS_INVOLVED);
    }

    public ParseQuery<User> getQueryUsersInvovled(){
        return relationFrame.getQuery(KEY_USERS_INVOLVED);
    }

    public List<User> getListUsersInvovled(){
        return relationFrame.getList(KEY_USERS_INVOLVED);
    }

    public Goal addUserInvovled(User user){
        return (Goal) relationFrame.add(KEY_USERS_INVOLVED, user);
    }

    public Goal removeUserInvovled(User user){
        return (Goal) relationFrame.remove(KEY_USERS_INVOLVED, user);
    }

    //Actions

    public ParseRelation<Action> getRelationAction(){
        return getRelation(KEY_ACTIONS);
    }

    public ParseQuery<Action> getQueryAction(){
        return relationFrame.getQuery(KEY_ACTIONS);
    }

    public List<Action> getListAction(){
        return relationFrame.getList(KEY_ACTIONS);
    }

    public Goal addAction(Action action){
        return (Goal) relationFrame.add(KEY_ACTIONS, action);
    }

    private Goal removeAction(Action action){
        return (Goal) relationFrame.remove(KEY_ACTIONS, action);
    }

    //Tags

    public ParseRelation<Tag> getRelationTags(){
        //Get the relation of tags
        return getRelation(KEY_TAGS);
    }

    public ParseQuery<Tag> getQueryTags(){
        //Get the parsequery for tags
        return relationFrame.getQuery(KEY_TAGS);
    }

    public List<Tag> getListTags(){
        return relationFrame.getList(KEY_TAGS);
    }

    public Goal addTag(Tag tag){
        return (Goal) relationFrame.add(KEY_TAGS, tag);
    }

    public Goal removeTag(Tag tag){
        return (Goal) relationFrame.remove(KEY_TAGS,tag);
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

