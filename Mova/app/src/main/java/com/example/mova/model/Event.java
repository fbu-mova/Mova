package com.example.mova.model;

import com.example.mova.RelationFrame;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.Date;
import java.util.List;

@ParseClassName("Event")
public class Event extends ParseObject {

    public static final String KEY_DATE = "date";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_EVENT_PIC = "eventPic";
    RelationFrame<Tag> relTags = new RelationFrame<>(this);
    //Date

    public Date getDate(){
        return getDate(KEY_DATE);
    }

    public Event setDate(Date date){
        put(KEY_DATE,date);
        return this;
    }

    //Event Pic

    public ParseFile getEventPic(){
        return getParseFile(KEY_EVENT_PIC);
    }

    public Event setEventPic(ParseFile file){
        put(KEY_EVENT_PIC,file);
        return this;
    }

    //Location

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public Event setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
        return this;
    }

    //Description

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public Event setDescription(String description){
        put(KEY_DESCRIPTION,description);
        return this;
    }

    //Title

    public String getTitle(){
        return getString(KEY_TITLE);
    }

    public Event setTitle(String title){
        put(KEY_TITLE,title);
        return this;
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
