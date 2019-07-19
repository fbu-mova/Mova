package com.example.mova.model;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_JOURNAL = "journal";
    public static final String KEY_GROUPS_IN = "groupsIn";
    public static final String KEY_SCRAPBOOK = "scrapbook";
    public static final String KEY_PROFILE_PIC = "profilePic";

    public final RelationFrame<User> relFriends = new RelationFrame<>(this, KEY_FRIENDS);
    public final RelationFrame<Post> relJournal = new RelationFrame<>(this, KEY_JOURNAL);
    public final  RelationFrame<Group> relGroups = new RelationFrame<>(this, KEY_GROUPS_IN);
    public final  RelationFrame<Post> relScrapbook = new RelationFrame<>(this, KEY_SCRAPBOOK);

    //Email verification

    public Boolean getEmailVerified(){
        return getBoolean(KEY_EMAIL_VERIFIED);
    }

    public User setEmailVerified(Boolean bool){
        put(KEY_EMAIL_VERIFIED, bool);
        return this;
    }

    //Profile Picture
    public ParseFile getProfilePic(){
        return getParseFile(KEY_PROFILE_PIC);
    }

    public User setProfilePic(ParseFile file){
        put(KEY_PROFILE_PIC, file);
        return this;
    }
}
