package com.example.mova.model;

import com.example.mova.RelationFrame;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.Date;
import java.util.List;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_GOALS = "goals";
    public static final String KEY_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ADMIN = "admin";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_GROUP_PIC = "groupPic";
    RelationFrame<Goal> relGoals = new RelationFrame<>(this);
    RelationFrame<User> relUsers = new RelationFrame<>(this);
    RelationFrame<Post> relPosts = new RelationFrame<>(this);
    RelationFrame<Event> relEvents = new RelationFrame<>(this);
    RelationFrame<User> relAdmins = new RelationFrame<>(this);
    RelationFrame<Tag> relTags = new RelationFrame<>(this);

    //Group Pic
    public ParseFile getGroupPic(){
        return getParseFile(KEY_GROUP_PIC);
    }

    public Group setGroupPic(ParseFile file){
        put(KEY_GROUP_PIC, file);
        return this;
    }

    //CreatedAt
    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    //Name
    public String getName(){
        return getString(KEY_NAME);
    }

    public Group setName(String name){
        put(KEY_NAME, name);
        return this;
    }

    //Description
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public Group setDescription(String description){
        put(KEY_DESCRIPTION,description);
        return this;
    }

    //Goals

    public ParseRelation<Goal> getRelationGoals(){
        //Get the relation of comments
        return getRelation(KEY_GOALS);
    }

    public ParseQuery<Goal> getQueryGoals(){
        //Get the parsequery for comments
        return relGoals.getQuery(KEY_GOALS);
    }

    public void getListGoals(AsyncUtils.ListCallback<Goal> callback) {
        relGoals.getList(KEY_GOALS, callback);
    }

    public void addGoal(Goal goal, AsyncUtils.ItemCallback<Goal> callback) {
        relGoals.add(KEY_GOALS,goal, callback);
    }

    public Goal removeGoal(Goal goal, AsyncUtils.EmptyCallback callback) {
        return relGoals.remove(KEY_GOALS, goal, callback);
    }

    //Users

    public ParseRelation<User> getRelationUser(){
        return getRelation(KEY_USERS);
    }

    public ParseQuery<User> getQueryUser(){
        return relUsers.getQuery(KEY_USERS);
    }

    public void getListUsers(AsyncUtils.ListCallback<User> callback) {
        relUsers.getList(KEY_USERS, callback);
    }

    public void addUser(User user, AsyncUtils.ItemCallback<User> callback) {
        relUsers.add(KEY_USERS, user, callback);
    }

    public User removeUser(User user, AsyncUtils.EmptyCallback callback) {
        return relUsers.remove(KEY_USERS, user, callback);
    }

    //Posts

    public ParseRelation<Post> getRelationPost(){
        return getRelation(KEY_POSTS);
    }

    public ParseQuery<Post> getQueryPost(){
        return relPosts.getQuery(KEY_POSTS);
    }

    public void getListPost(AsyncUtils.ListCallback<Post> callback) {
        relPosts.getList(KEY_POSTS, callback);
    }

    public void addPost(Post post, AsyncUtils.ItemCallback<Post> callback) {
        relPosts.add(KEY_POSTS, post, callback);
    }

    public Post removePost(Post post, AsyncUtils.EmptyCallback callback) {
        return relPosts.remove(KEY_POSTS, post, callback);
    }

    //Events

    public ParseRelation<Event> getRelationEvent(){
        return getRelation(KEY_EVENTS);
    }

    public ParseQuery<Event> getQueryEvent(){
        return relEvents.getQuery(KEY_EVENTS);
    }

    public void getListEvents(AsyncUtils.ListCallback<Event> callback) {
        relEvents.getList(KEY_EVENTS, callback);
    }

    public void addEvent(Event event, AsyncUtils.ItemCallback<Event> callback) {
        relEvents.add(KEY_EVENTS,event, callback);
    }

    public Event removeEvent(Event event, AsyncUtils.EmptyCallback callback) {
        return relEvents.remove(KEY_EVENTS,event, callback);
    }

    //Admins

    public ParseRelation<User> getRelationAdmin(){
        return getRelation(KEY_ADMIN);
    }

    public ParseQuery<User> getQueryAdmin(){
        return relAdmins.getQuery(KEY_ADMIN);
    }

    public void getListAdmins(AsyncUtils.ListCallback<User> callback) {
        relAdmins.getList(KEY_ADMIN, callback);
    }

    public void addAdmin(User admin, AsyncUtils.ItemCallback<User> callback) {
        relAdmins.add(KEY_ADMIN, admin, callback);
    }

    public User removeAdmin(User admin, AsyncUtils.EmptyCallback callback) {
        return relAdmins.remove(KEY_ADMIN, admin, callback);
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
