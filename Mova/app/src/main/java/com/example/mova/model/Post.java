package com.example.mova.model;


import com.example.mova.Mood;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import org.w3c.dom.Comment;

@ParseClassName("Post")
public class Post extends ParseObject{
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_PERSONAL = "isPersonal";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_BODY = "body";
    public static final String KEY_MOOD = "mood";
    public static final String KEY_IMAGE = "embeddedImage";
    public static final String KEY_TAGS = "tags";
    RelationFrame<Post> relComments = new RelationFrame<>(this, KEY_COMMENTS);
    RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);

    public boolean getIsPersonal(){
        return getBoolean(KEY_IS_PERSONAL);
    }

    public Post setIsPersonal(Boolean bool){
        put(KEY_IS_PERSONAL, bool);
        return this;
    }

    public User getAuthor(){
        return (User) getParseUser(KEY_AUTHOR);
    }

    public Post setAuthor(User user){
        put(KEY_AUTHOR, user);
        return this;
    }

    public Group getGroup(){
        return (Group) getParseObject(KEY_GROUP);
    }

    public Post setGroup(Group group){
        put(KEY_GROUP, group);
        return this;
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public Post setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
        return this;
    }

    public Mood.Status getMood() {
        String moodValue = getString(KEY_MOOD);
        return (moodValue == null) ? null : Mood.Status.valueOf(moodValue);
    }

    public Post setMood(Mood.Status mood) {
        put(KEY_MOOD, mood.toString());
        return this;
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public Post setBody(String body) {
        put(KEY_BODY, body);
        return this;
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public Post setImage(ParseFile file){
        put(KEY_IMAGE, file);
        return this;
    }

    //Comments
    public ParseRelation<Post> getRelationComments(){
        //Get the relation of comments
        return getRelation(KEY_COMMENTS);
    }

    public ParseQuery<Post> getQueryComments(){
        //Get the parsequery for comments
        return relComments.getQuery();
    }

    public void getListComments(AsyncUtils.ListCallback<Post> callback) {
        relComments.getList(callback);
    }

    public void addComment(Post comment, AsyncUtils.ItemCallback<Post> callback) {
        relComments.add(comment, callback);
    }

    public Post removeComment(Post comment, AsyncUtils.EmptyCallback callback) {
        return relComments.remove(comment, callback);
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

}
