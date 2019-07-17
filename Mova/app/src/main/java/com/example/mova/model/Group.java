package com.example.mova.model;

import com.example.mova.RelationFrame;
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
    RelationFrame relationFrame = new RelationFrame();

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
        return relationFrame.getQuery(KEY_GOALS);
    }

    public List<Goal> getListGoals(){
        return relationFrame.getList(KEY_GOALS);
    }

    public Group addGoal(Goal goal){
        return (Group) relationFrame.add(KEY_GOALS,goal);
    }

    public Group removeGoal(Goal goal){
        return (Group) relationFrame.remove(KEY_GOALS, goal);
    }

    //Users

    public ParseRelation<User> getRelationUser(){
        return getRelation(KEY_USERS);
    }

    public ParseQuery<User> getQueryUser(){
        return relationFrame.getQuery(KEY_USERS);
    }

    public List<User> getListUsers(){
        return relationFrame.getList(KEY_USERS);
    }

    public Group addUser(User user){
        return (Group) relationFrame.add(KEY_USERS, user);
    }

    public Group removeUser(User user){
        return (Group) relationFrame.remove(KEY_USERS, user);
    }

    //Posts

    public ParseRelation<Post> getRelationPost(){
        return getRelation(KEY_POSTS);
    }

    public ParseQuery<Post> getQueryPost(){
        return relationFrame.getQuery(KEY_POSTS);
    }

    public List<Post> getListPosts(){
        return relationFrame.getList(KEY_POSTS);
    }


    public Group addPost(Post post){
        return (Group) relationFrame.add(KEY_POSTS, post);
    }

    public Group removePost(Post post){
        return (Group) relationFrame.remove(KEY_POSTS, post);
    }

    //Events

    public ParseRelation<Event> getRelationEvent(){
        return getRelation(KEY_EVENTS);
    }

    public ParseQuery<Event> getQueryEvent(){
        return relationFrame.getQuery(KEY_EVENTS);
    }

    public List<Event> getListEvent(){
        return relationFrame.getList(KEY_EVENTS);
    }

    public Group addEvents(Event event){
        return (Group) relationFrame.add(KEY_EVENTS,event);
    }

    public Group removeEvents(Event event){
        return (Group) relationFrame.remove(KEY_EVENTS,event);
    }

    //Admins

    public ParseRelation<User> getRelationAdmin(){
        return getRelation(KEY_ADMIN);
    }

    public ParseQuery<User> getQueryAdmin(){
        return relationFrame.getQuery(KEY_ADMIN);
    }

    public List<User> getListAdmin(){
        return relationFrame.getList(KEY_ADMIN);
    }

    public Group addAdmin(User admin){
        return (Group) relationFrame.add(KEY_ADMIN, admin);
    }

    public Group removeAdmin(User admin){
        return (Group) relationFrame.remove(KEY_ADMIN, admin);
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

    public Group addTag(Tag tag){
        return (Group) relationFrame.add(KEY_TAGS, tag);
    }

    public Group removeTag(Tag tag){
        return (Group) relationFrame.remove(KEY_TAGS,tag);
    }
}
