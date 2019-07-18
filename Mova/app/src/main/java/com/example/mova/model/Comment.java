package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

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

    public Comment setBody(String body){
        put(KEY_BODY, body);
        return this;
    }

    //Image
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public Comment setImage(ParseFile file){
        put(KEY_IMAGE, file);
        return this;
    }

    //ParentPost
    public Post getParentPost(){
        return (Post) getParseObject(KEY_PARENT_POST);
    }

    public Comment setParentPost(Post post){
        put(KEY_PARENT_POST, post);
        post.addComment(this, (comment) -> {});
        post.saveInBackground();
        return this;
    }

    //Commenter
    public User getCommenter(){
        return (User) getParseUser(KEY_COMMENTER);
    }

    public Comment setCommenter(User user){
        put(KEY_COMMENTER, user);
        return this;
    }
}
