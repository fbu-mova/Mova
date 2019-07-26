package com.example.mova.model;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.utils.AsyncUtils;
import com.example.mova.utils.StableNumericalIdProvider;
import com.example.mova.utils.TimeUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    public static final String KEY_PROFILE_PIC = "profilePic";

    public static final String KEY_GOALS = "goals";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_JOURNAL = "journal";
    public static final String KEY_GROUPS_IN = "groupsIn";
    public static final String KEY_ADMIN = "adminOf";
    public static final String KEY_SCRAPBOOK = "scrapbook";
    public static final String KEY_POSTS = "posts";


    public final RelationFrame<Goal> relGoals = new RelationFrame<>(this, KEY_GOALS);
    public final RelationFrame<User> relFriends = new RelationFrame<>(this, KEY_FRIENDS);
    public final RelationFrame<Event> relEvents = new RelationFrame<>(this, KEY_EVENTS);
    public final RelationFrame<Post> relJournal = new RelationFrame<>(this, KEY_JOURNAL);
    public final RelationFrame<Group> relGroups = new RelationFrame<>(this, KEY_GROUPS_IN);
    public final RelationFrame<Group> relAdminOf = new RelationFrame<>(this, KEY_ADMIN);
    public final RelationFrame<Post> relScrapbook = new RelationFrame<>(this, KEY_SCRAPBOOK);
    public final RelationFrame<Post> relPosts = new RelationFrame<>(this, KEY_POSTS);

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

    public void postJournalEntry(Post journalEntry, List<Tag> tags, AsyncUtils.ItemCallback<Post> callback) {
        postJournalEntry(journalEntry, tags, null, callback);
    }

    public void postJournalEntry(Post journalEntry, List<Tag> tags, Media media, AsyncUtils.ItemCallback<Post> callback) {
        journalEntry.savePost(tags, media, (post) -> relJournal.add(journalEntry, callback));
    }

    @Override
    public int hashCode() {
        return HashableParseObject.getHashCode(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return HashableParseObject.equals(this, obj);
    }
}
