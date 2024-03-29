package com.example.mova.model;

import android.app.Activity;
import android.util.Log;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.icons.Icons;
import com.example.mova.icons.NounProjectClient;
import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.ColorUtils;
import com.example.mova.utils.Wrapper;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Goal")
public class Goal extends HashableParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_AUTHOR = "author";
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

    public static final String KEY_ICON_ID = "nounIconId";
    public static final String KEY_HUE = "hue";
    public static final String KEY_UPDATED_AT = "updatedAt";

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

    public void getGroupFull(AsyncUtils.EmptyCallback empty, AsyncUtils.ItemCallback<Group> callback){
        Group group = getGroup();
        if(group == null){
            empty.call();
            return;
        }
        group.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e != null){
                    Log.e("GoalUtils", "Error with  user list query");
                    e.printStackTrace();
                    return;
                }
                callback.call((Group) object);
            }
        });
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

    // Icon
    public void getNounIcon(DelegatedResultActivity activity, AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> callback) {
        int id = getInt(KEY_ICON_ID);
        Icons.from(activity).nounIcon(id, callback);
    }

    public Goal setNounIcon(NounProjectClient.Icon icon) {
        put(KEY_ICON_ID, icon.id);
        return this;
    }

    public int getNounIconId() {
        return getInt(KEY_ICON_ID);
    }

    public Goal setNounIconId(int iconId) {
        put(KEY_ICON_ID, iconId);
        return this;
    }

    // Hue
    public ColorUtils.Hue getHue() {
        String str = getString(KEY_HUE);
        if (str != null) return ColorUtils.Hue.valueOf(str);
        ColorUtils.Hue hue = ColorUtils.Hue.random();
        setHue(hue);
        return hue;
    }

    public Goal setHue(ColorUtils.Hue hue) {
        put(KEY_HUE, hue.toString());
        return this;
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

