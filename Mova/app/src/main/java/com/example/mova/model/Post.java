package com.example.mova.model;


import com.example.mova.Mood;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONObject;
import org.w3c.dom.Comment;

@ParseClassName("Post")
public class Post extends HashableParseObject {

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_PERSONAL = "isPersonal";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_BODY = "body";
    public static final String KEY_MOOD = "mood";
    public static final String KEY_IMAGE = "embeddedImage";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_PARENT_POST = "parentPost";

    //Relations
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_TAGS = "tags";
    public final RelationFrame<Post> relComments = new RelationFrame<>(this, KEY_COMMENTS);
    public final RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);

    //Author
    public User getAuthor(){
        return (User) getParseUser(KEY_AUTHOR);
    }

    public Post setAuthor(User user){
        put(KEY_AUTHOR, user);
        return this;
    }

    //Group
    public Group getGroup(){
        return (Group) getParseObject(KEY_GROUP);
    }

    public Post setGroup(Group group){
        put(KEY_GROUP, group);
        return this;
    }

    //isPersonal
    public boolean getIsPersonal(){
        return getBoolean(KEY_IS_PERSONAL);
    }

    public Post setIsPersonal(Boolean bool){
        put(KEY_IS_PERSONAL, bool);
        return this;
    }


    //location
    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public Post setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
        return this;
    }


    //body
    public String getBody() {
        return getString(KEY_BODY);
    }

    public Post setBody(String body) {
        put(KEY_BODY, body);
        return this;
    }

    //mood
    public Mood.Status getMood() {
        String moodValue = getString(KEY_MOOD);
        return (moodValue == null) ? null : Mood.Status.valueOf(moodValue);
    }

    public Post setMood(Mood.Status mood) {
        put(KEY_MOOD, mood.toString());
        return this;
    }

    //Image
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public Post setImage(ParseFile file){
        put(KEY_IMAGE, file);
        return this;
    }

    // Media
    public Media getMedia() {
        return (Media) getParseObject(KEY_MEDIA);
    }

    public Post setMedia(Media media) {
        put(KEY_MEDIA, media);
        return this;
    }

    public Post removeMedia() {
        put(KEY_MEDIA, JSONObject.NULL);
        return this;
    }

    // Parent post
    public Post getParent() {
        return (Post) getParseObject(KEY_PARENT_POST);
    }

    public Post setParent(Post parent) {
        put(KEY_PARENT_POST, parent);
        return this;
    }

    public Post removeParent() {
        put(KEY_PARENT_POST, JSONObject.NULL);
        return this;
    }
}
