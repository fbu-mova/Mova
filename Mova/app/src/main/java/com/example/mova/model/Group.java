package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Group")
public class Group extends ParseObject {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GROUP_PIC = "groupPic";

    //Relations
    public static final String KEY_GOALS = "goals";
    public static final String KEY_ADMIN = "admin";
    public static final String KEY_USERS = "users";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_TAGS = "tags";


    RelationFrame<Goal> relGoals = new RelationFrame<>(this, KEY_GOALS);
    RelationFrame<User> relAdmins = new RelationFrame<>(this, KEY_ADMIN);
    RelationFrame<User> relUsers = new RelationFrame<>(this, KEY_USERS);
    RelationFrame<Event> relEvents = new RelationFrame<>(this, KEY_EVENTS);
    RelationFrame<Post> relPosts = new RelationFrame<>(this, KEY_POSTS);
    RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);


    //CreatedAt
    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    //Name
    public String getName() {
        return getString(KEY_NAME);
    }

    public Group setName(String name) {
        put(KEY_NAME, name);
        return this;
    }

    //Description
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public Group setDescription(String description) {
        put(KEY_DESCRIPTION, description);
        return this;
    }

    //Group Pic
    public ParseFile getGroupPic() {
        return getParseFile(KEY_GROUP_PIC);
    }

    public Group setGroupPic(ParseFile file) {
        put(KEY_GROUP_PIC, file);
        return this;
    }
}