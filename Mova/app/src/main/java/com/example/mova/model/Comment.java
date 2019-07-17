package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_BODY = "body";
    public static final String KEY_IMAGE = "embeddedImage";
    public static final String KEY_PARENT_POST = "parentPost";
    public static final String KEY_COMMENTER = "commenter";

    //CreatedAt
    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    //Body
    public String getBody(){
        return getString(KEY_BODY);
    }

    public void setBody(String body){
        put(KEY_BODY, body);
    }

    //Image
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile file){
        put(KEY_IMAGE, file);
    }

    //ParentPost
    public Post getParentPost(){
        return (Post) getParseObject(KEY_PARENT_POST);
    }

    public void setParentPost(Post post){
        put(KEY_PARENT_POST, post);
        post.addComment(this);
        post.saveInBackground();
    }

    //Commenter
    public ParseUser getCommenter(){
        return getParseUser(KEY_COMMENTER);
    }

    public void setCommenter(ParseUser user){
        put(KEY_COMMENTER, user);
    }
}
