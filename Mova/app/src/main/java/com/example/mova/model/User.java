package com.example.mova.model;

import com.example.mova.RelationFrame;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_JOURNAL = "journal";
    public static final String KEY_GROUPS_IN = "groupsIn";
    public static final String KEY_SCRAPBOOK = "scrapbook";
    public static final String KEY_PROFILE_PIC = "profilePic";
    protected RelationFrame<User> relFriend = new RelationFrame<>(this);
    protected RelationFrame<Post> relJournal = new RelationFrame<>(this);
    protected RelationFrame<Group> relGroups = new RelationFrame<>(this);
    protected RelationFrame<Post> relScrapbook = new RelationFrame<>(this);

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


    //Friends

    public ParseRelation<User> getRelationFriend(){
        return getRelation(KEY_FRIENDS);
    }

    public ParseQuery<User> getQueryFriend(){
        return relFriend.getQuery(KEY_FRIENDS);
    }

    public void getListFriends(AsyncUtils.ListCallback<User> callback) {
        relFriend.getList(KEY_FRIENDS, callback);
    }

    public void addFriend(User friend, AsyncUtils.ItemCallback<User> callback) {
        relFriend.add(KEY_FRIENDS, friend, callback);
    }

    public User removeFriend(User friend, AsyncUtils.EmptyCallback callback) {
        return relFriend.remove(KEY_FRIENDS, friend, callback);
    }

    //Journal

    public ParseRelation<Post> getRelationJournal(){
        return getRelation(KEY_JOURNAL);
    }

    public ParseQuery<Post> getQueryJournal(){
        return relJournal.getQuery(KEY_JOURNAL);
    }

    public void getListJournal(AsyncUtils.ListCallback<Post> callback) {
        relJournal.getList(KEY_JOURNAL, callback);
    }

    public void addJournalPost(Post journalPost, AsyncUtils.ItemCallback<Post> callback) {
        relJournal.add(KEY_JOURNAL, journalPost, callback);
    }

    public Post removeJournalPost(Post journalPost, AsyncUtils.EmptyCallback callback) {
        return relJournal.remove(KEY_JOURNAL, journalPost, callback);
    }

    //groups in

    public ParseRelation<Group> getRelationGroupsIn(){
        return getRelation(KEY_GROUPS_IN);
    }

    public ParseQuery<Group> getQueryGroupsIn(){
        return relGroups.getQuery(KEY_GROUPS_IN);
    }

    public void getListGroupsIn(AsyncUtils.ListCallback<Group> callback) {
        relGroups.getList(KEY_GROUPS_IN, callback);
    }

    public void joinGroup(Group group, AsyncUtils.ItemCallback<Group> callback) {
        relGroups.add(KEY_GROUPS_IN,group, callback);
    }

    public Group leaveGroup(Group group, AsyncUtils.EmptyCallback callback) {
        return relGroups.remove(KEY_GROUPS_IN, group, callback);
    }

    //Scrapbook

    public ParseRelation<Post> getRelationScrapbook(){
        return getRelation(KEY_SCRAPBOOK);
    }

    public ParseQuery<Post> getQueryScrapbook(){
        return relScrapbook.getQuery(KEY_SCRAPBOOK);
    }

    public void getListScrapbook(AsyncUtils.ListCallback<Post> callback) {
        relScrapbook.getList(KEY_SCRAPBOOK, callback);
    }

    public void addScrapbookPost(Post scrapbookPost, AsyncUtils.ItemCallback<Post> callback) {
        relScrapbook.add(KEY_SCRAPBOOK, scrapbookPost, callback);
    }

    public Post removeScrapbookPost(Post scrapbookPost, AsyncUtils.EmptyCallback callback) {
        return relScrapbook.remove(KEY_SCRAPBOOK, scrapbookPost, callback);
    }
}
