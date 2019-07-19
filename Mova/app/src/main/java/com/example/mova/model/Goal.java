package com.example.mova.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Goal")
public class Goal extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_COLOR = "color";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IS_PERSONAL = "isPersonal";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_FROM_GROUP = "fromGroup";

    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_SHARED_ACTIONS = "sharedActions";
    public static final String KEY_TAGS = "tags";

    public final RelationFrame<User> relUsersInvolved = new RelationFrame<>(this, KEY_USERS_INVOLVED);
    public final RelationFrame<Action> relActions = new RelationFrame<>(this, KEY_ACTIONS);
    public final RelationFrame<SharedAction> relSharedActions = new RelationFrame<>(this, KEY_SHARED_ACTIONS);
    public final RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);
    ;

    //Title

    public String getTitle(){
        return getString(KEY_TITLE);
    }

    public Goal setTitle(String title){
        put(KEY_TITLE,title);
        return this;
    }

    //Author

    public User getAuthor(){
        return (User) getParseUser(KEY_AUTHOR);
    }

    public Goal setAuthor(User user){
        put(KEY_AUTHOR, user);
        return this;
    }

    //Color
    public String getColor(){
        return getString(KEY_COLOR);
    }

    public Goal setColor(String color){
        put(KEY_COLOR, color);
        return this;
    }


    //Image

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public Goal setImage(ParseFile file){
        put(KEY_IMAGE, file);
        return this;
    }

    //Ispersonal

    public boolean getIsPersonal(){
        return getBoolean(KEY_IS_PERSONAL);
    }

    public Goal setIsPersonal(Boolean bool){
        put(KEY_IS_PERSONAL, bool);
        return this;
    }

    //Description

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public Goal setDescription(String description){
        put(KEY_DESCRIPTION,description);
        return this;
    }

    //fromGroup
    public Group getGroup(){
        return (Group) getParseObject(KEY_FROM_GROUP);
    }

    public String getFromGroupName() {
        Group group = getGroup();
        if (group == null) {
            return "";
        }

        return group.getName();
    }


    public Goal setGroup(Group group){
        put(KEY_FROM_GROUP,group);
        return this;
    }

    public static class Query extends ParseQuery<Goal> {

        public Query() {
            super(Goal.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;

        }
        public Query withGroup() {
            include(KEY_FROM_GROUP);
            return this;
        }
    }
}

