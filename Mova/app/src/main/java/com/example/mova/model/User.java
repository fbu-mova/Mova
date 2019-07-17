package com.example.mova.model;

import com.example.mova.RelationFrame;
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
    RelationFrame relationFrame = new RelationFrame(this);

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
        return relationFrame.getQuery(KEY_FRIENDS);
    }

    public List<User> getListFriends(){
        return relationFrame.getList(KEY_FRIENDS);
    }

    public User addFriend(User friend){
        return (User) relationFrame.add(KEY_FRIENDS, friend);
    }

    public User removeFriend(User friend){
        return (User) relationFrame.remove(KEY_FRIENDS, friend);
    }

    //Journal

    public ParseRelation<Post> getRelationJournal(){
        return getRelation(KEY_JOURNAL);
    }

    public ParseQuery<Post> getQueryJournal(){
        return relationFrame.getQuery(KEY_JOURNAL);
    }

    public List<Post> getListJournal(){
        return relationFrame.getList(KEY_JOURNAL);
    }


    public User addJournalPost(Post journalPost){
        return (User) relationFrame.add(KEY_JOURNAL, journalPost);
    }

    public User removeJournalPost(Post journalPost){
        return (User) relationFrame.remove(KEY_JOURNAL, journalPost);
    }

    //groups in

    public ParseRelation<Group> getRelationGroupsIn(){
        return getRelation(KEY_GROUPS_IN);
    }

    public ParseQuery<Group> getQueryGroupsIn(){
        return relationFrame.getQuery(KEY_GROUPS_IN);
    }

    public List<Group> getListGroupsIn(){
        return relationFrame.getList(KEY_GROUPS_IN);
    }

    public User joinGroup(Group group){
        return (User) relationFrame.add(KEY_GROUPS_IN,group);
    }

    public User leaveGroup(Group group){
        return (User) relationFrame.remove(KEY_GROUPS_IN,group);
    }

    //Scrapbook

    public ParseRelation<Post> getRelationScrapbook(){
        return getRelation(KEY_SCRAPBOOK);
    }

    public ParseQuery<Post> getQueryScrapbook(){
        return relationFrame.getQuery(KEY_SCRAPBOOK);
    }

    public List<Post> getListScrapbook(){
        return relationFrame.getList(KEY_SCRAPBOOK);
    }


    public User addScrapbookPost(Post scrapbookPost){
        return (User) relationFrame.add(KEY_SCRAPBOOK, scrapbookPost);
    }

    public User removeScrapbookPost(Post scrapbookPost){
        return (User) relationFrame.remove(KEY_SCRAPBOOK, scrapbookPost);
    }
}
