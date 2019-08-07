package com.example.mova.model;

import com.example.mova.icons.Icons;
import com.example.mova.icons.NounProjectClient;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.Date;

@ParseClassName("Group")
public class Group extends HashableParseObject {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_GROUP_PIC = "groupPic";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_ICON_ID = "nounIconId";

    //Relations
    public static final String KEY_GOALS = "goals";
    public static final String KEY_ADMIN = "admin";
    public static final String KEY_USERS = "users";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_POSTS = "posts";
    public static final String KEY_TAGS = "tags";

    public final RelationFrame<Goal> relGoals = new RelationFrame<>(this, KEY_GOALS);
    public final RelationFrame<User> relAdmins = new RelationFrame<>(this, KEY_ADMIN);
    public final RelationFrame<User> relUsers = new RelationFrame<>(this, KEY_USERS);
    public final RelationFrame<Event> relEvents = new RelationFrame<>(this, KEY_EVENTS);
    public final RelationFrame<Post> relPosts = new RelationFrame<>(this, KEY_POSTS);
    public final RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);

    //CreatedAt
    public Date getCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    //Name
    public String getName() {
        return getString(KEY_NAME);
    }

    public Group setName(String name) {
        put(KEY_NAME, name);
        return this;
    }

    //Description
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public Group setDescription(String description) {
        put(KEY_DESCRIPTION, description);
        return this;
    }

    //Group Pic
    public ParseFile getGroupPic() {
        return getParseFile(KEY_GROUP_PIC);
    }

    public Group setGroupPic(ParseFile file) {
        put(KEY_GROUP_PIC, file);
        return this;
    }

    //Members
    public int getMemberCount(){
        return getInt(KEY_MEMBERS);
    }

    public Group setMemberCount(int count){
        put(KEY_MEMBERS, count);
        return this;
    }

    public Group increaseMemberCount(int increase){
        int memberCount = getMemberCount();
        memberCount += increase;
        put(KEY_MEMBERS, memberCount);
        return this;
    }

    // Icon
    public void getNounIcon(AsyncUtils.TwoItemCallback<NounProjectClient.Icon, Throwable> callback) {
        int id = getInt(KEY_ICON_ID);
        Icons.nounIcon(id, callback);
    }

    public Group setNounIcon(NounProjectClient.Icon icon) {
        put(KEY_ICON_ID, icon.id);
        return this;
    }

    public static class Query extends ParseQuery<Group> {

        public Query() {
            super(Group.class);
        }

        public Query getGroup(String name) {
            whereEqualTo(KEY_NAME, name);
            return this;
        }
    }
}