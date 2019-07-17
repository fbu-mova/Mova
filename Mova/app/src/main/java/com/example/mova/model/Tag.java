package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Tag")
public class Tag extends ParseObject {

    public static final String KEY_NAME = "name";

    Tag(String name){
        this.setName(name);
    }

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }
}
