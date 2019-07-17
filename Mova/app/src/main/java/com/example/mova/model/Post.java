package com.example.mova.model;


import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject{
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_PERSONAL = "isPersonal";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_BODY = "body";
    public static final String KEY_IMAGE = "embeddedImage";
    public static final String KEY_TAGS = "tags";

    public boolean getIsPersonal(){
        return getBoolean(KEY_IS_PERSONAL);
    }

    public Post setIsPersonal(Boolean bool){
        put(KEY_IS_PERSONAL, bool);
        return this;
    }

    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }

    public Post setAuthor(ParseUser user){
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

    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
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

    public void setImage(ParseFile file){
        put(KEY_IMAGE, file);
    }

    //Comments
    public ParseRelation<Comment> getRelationComments(){
        //Get the relation of comments
        return getRelation(KEY_COMMENTS);
    }

    public ParseQuery<Comment> getQueryComments(){
        //Get the parsequery for comments
        return (ParseQuery<Comment>) (Object) getRelation(KEY_COMMENTS).getQuery();
    }

    public List<Comment> getListComments(){
        ParseQuery<Comment> commentsR = getQueryComments();
        List<Comment> commentList = new ArrayList<Comment>();
        commentsR.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if(e != null){
                    Log.e("Post","error retriving post list");
                }
                commentList.addAll(objects);
            }
        });
        return commentList;
    }

    public void addComment(Comment comment){
        ParseRelation<Comment> comments = getRelationComments();
        comments.add(comment);
        this.put(KEY_COMMENTS, comments);
        this.saveInBackground();
    }

    public void removeComment(Comment comment){
        ParseRelation<Comment> comments = getRelationComments();
        comments.remove(comment);
        this.put(KEY_COMMENTS, comments);
        this.saveInBackground();
    }

    //Tags
    public ParseRelation<Tag> getRelationTags(){
        //Get the relation of tags
        return getRelation(KEY_TAGS);
    }

    public ParseQuery<Tag> getQueryTags(){
        //Get the parsequery for tags
        return (ParseQuery<Tag>) (Object) getRelation(KEY_TAGS).getQuery();
    }

    public List<Tag> getListTags(){
        ParseQuery<Tag> tagR = getQueryTags();
        List<Tag> tagList = new ArrayList<Tag>();
        tagR.findInBackground(new FindCallback<Tag>() {
            @Override
            public void done(List<Tag> objects, ParseException e) {
                if(e != null){
                    Log.e("Post","error retriving post list");
                }
                tagList.addAll(objects);
            }
        });
        return tagList;
    }

    public void addTag(Tag tag){
        ParseRelation<Tag> tags = getRelationTags();
        tags.add(tag);
        this.put(KEY_TAGS, tags);
        this.saveInBackground();
    }

    public void removeTag(Tag tag){
        ParseRelation<Tag> tags = getRelationTags();
        tags.remove(tag);
        this.put(KEY_TAGS, tags);
        this.saveInBackground();
    }

}
