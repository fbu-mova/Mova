package com.example.mova.model;


import com.parse.ParseClassName;
import com.parse.ParseUser;
import com.parse.ParseObject;

@ParseClassName("Post")
public class Post {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_GROUP = "group";

    public void setAuthor(ParseUser user){
        put(KEY_AUTHOR, user);
    }
}
