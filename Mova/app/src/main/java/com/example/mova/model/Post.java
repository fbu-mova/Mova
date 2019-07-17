package com.example.mova.model;


import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;


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

    public boolean getIsPersonal(){
        return getBoolean(KEY_IS_PERSONAL);
    }

    public void setIsPersonal(Boolean bool){
        put(KEY_IS_PERSONAL,bool);
    }

    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser user){
        put(KEY_AUTHOR, user);
    }

    public Group getGroup(){
        return (Group) getParseObject(KEY_GROUP);
    }

    public void setGroup(Group group){
        put(KEY_GROUP, group);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
    }

    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    public void setCreatedAt(Date date) {
        put(KEY_CREATED_AT, date);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public List<Comment> getComments(){
        return (List<Comment>) (Object) getList(KEY_COMMENTS);
    }

    public void addComment(Comment comment){
        List<Comment> comments = getComments();
        comments.add(comment);
        this.put(KEY_COMMENTS, comments);
    }

    public void removeComment(Comment comment){
        int position = 0;
        List<Comment> comments = getComments();
        for(int i = 0; i < getComments().size(); i++){
            if(comment.getObjectId().equals(comments.get(i).getObjectId())){
                position = i;
            }
        }
        comments.remove(position);
        this.put(KEY_COMMENTS, comments);
    }

}
