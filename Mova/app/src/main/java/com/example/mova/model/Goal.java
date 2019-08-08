package com.example.mova.model;

import android.util.Log;

import com.example.mova.utils.AsyncUtils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Goal")
public class Goal extends HashableParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_COLOR = "color";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IS_PERSONAL = "isPersonal";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_FROM_GROUP = "fromGroup";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_OBJECT_ID = "objectId";

    public static final String KEY_USERS_INVOLVED = "usersInvolved";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_SHARED_ACTIONS = "sharedActions";
    public static final String KEY_TAGS = "tags";

    public final RelationFrame<User> relUsersInvolved = new RelationFrame<>(this, KEY_USERS_INVOLVED);
    public final RelationFrame<Action> relActions = new RelationFrame<>(this, KEY_ACTIONS);
    public final RelationFrame<SharedAction> relSharedActions = new RelationFrame<>(this, KEY_SHARED_ACTIONS);
    public final RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);



    //Title

    public String getTitle(){
        return getString(KEY_TITLE);
    }

    public Goal setTitle(String title){
        put(KEY_TITLE,title);
        return this;
    }

    //Author
    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);
    }

    public void getAuthor(Goal goal, AsyncUtils.ItemCallback<User> callback){
        ParseQuery<User> query = ParseQuery.getQuery("_User");
        query.whereEqualTo(KEY_OBJECT_ID, goal.getParseUser(KEY_AUTHOR).getObjectId())
                .findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> objects, ParseException e) {
                        if (e == null && objects.size() == 1) {
                            callback.call(objects.get(0));
                        }
                        else {
                            Log.e("goal model", "something wrong in get author", e);
                        }
                    }
                });
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

    public void getGroupName(AsyncUtils.EmptyCallback empty, AsyncUtils.ItemCallback<String> callback) {
        Group group = getGroup();
        if (group == null) {
            empty.call();
            return;
        }
        group.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                String name;
                if (e == null && object != null) {
                    name = ((Group) object).getName();

                }
                else {
                    Log.e("Goal model", "getGroupName failed or group null", e);
                    name = "";
                }
                callback.call(name);
        }

        });
    }


    public Goal setGroup(Group group){
        put(KEY_FROM_GROUP,group);
        return this;
    }

    //Members
    public int getMemberCount(){
        return getInt(KEY_MEMBERS);
    }

    public Goal setMemberCount(int count){
        put(KEY_MEMBERS, count);
        return this;
    }

    public Goal increaseMemberCount(int increase){
        int memberCount = getMemberCount();
        memberCount += increase;
        put(KEY_MEMBERS, memberCount);
        return this;
    }

    public interface HandleUpdatedProgress {
        void call(float updatedProgress);
    }

    public static class Query extends ParseQuery<Goal> {

        public Query() {
            super(Goal.class);
        }

        public Query getTop() {
            return getTop(20);
        }

        public Query getTop(int limit) {
            setLimit(limit);
            return this;
        }

        public Query withGroup() {
            include(KEY_FROM_GROUP);
            return this;
        }

        public Query fromCurrentUser() {
            whereEqualTo(KEY_AUTHOR, User.getCurrentUser());
            return this;
        }

        public Query withUser() {
            include(KEY_AUTHOR);
            return this;
        }
    }


    public static class GoalData {
        public Goal goal;
        public boolean userIsInvolved;

        public GoalData(Goal goal, boolean userIsInvolved) {
            this.goal = goal;
            this.userIsInvolved = userIsInvolved;
        }
    }


}

