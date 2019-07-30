package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.util.Date;

@ParseClassName("Event")
public class Event extends HashableParseObject {

    public static final String KEY_DATE = "date";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_EVENT_PIC = "eventPic";
    public static final String KEY_PARTICIPANTS = "participants";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_GROUPS = "groups";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_PARENT_GROUP = "parentGroup";
    public static final String KEY_HOST_USER = "hostUser";
    public final RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);
    public final RelationFrame<User> relParticipants = new RelationFrame<>(this, KEY_PARTICIPANTS);
    public final RelationFrame<Group> relGroup = new RelationFrame<>(this, KEY_GROUPS);
    public final RelationFrame<Post> relComments = new RelationFrame<>(this, KEY_COMMENTS);

    //Date
    public Date getDate(){
        return getDate(KEY_DATE);
    }

    public Event setDate(Date date){
        put(KEY_DATE,date);
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

    //Event Pic

    public ParseFile getEventPic(){
        return getParseFile(KEY_EVENT_PIC);
    }

    public Event setEventPic(ParseFile file){
        put(KEY_EVENT_PIC,file);
        return this;
    }

    //Parent Group

    public Group getParentGroup(){
        return  (Group) getParseObject(KEY_PARENT_GROUP);
    }
    public void getParentGroupName(Group group, AsyncUtils.ItemCallback<String> callback){

        group.fetchIfNeededInBackground(new GetCallback<Group>() {
            @Override
            public void done(Group object, ParseException e) {
                callback.call(object.getName());
            }
        });
    }

    public Event setParentGroup(Group g){
        put(KEY_PARENT_GROUP, g);
        return this;
    }

    //Host user
    public User getHost(){
        return (User) getParseObject(KEY_HOST_USER);
    }

    public Event setHost(User user){
        put(KEY_HOST_USER, user);
        return this;
    }

    public boolean isHostUser(User user){
        if(this.getHost().equals(user)){
            return true;
        }
        return false;
    }

}
