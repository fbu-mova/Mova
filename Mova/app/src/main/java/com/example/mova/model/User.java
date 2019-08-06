package com.example.mova.model;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mova.utils.PostConfig;
import com.example.mova.utils.AsyncUtils;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    public static final String KEY_LOCATION = "Location";

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

    //Location
    public ParseGeoPoint getLocation(){return getParseGeoPoint(KEY_LOCATION);}

    public void isFriendsWith(User user, AsyncUtils.ItemCallback<Boolean> callback){
        getFriendsList((friendList) -> {
            for( User friend : friendList){
                if(friend.getObjectId().equals(user.getObjectId())){
                    callback.call(true);
                    return;
                }
            }
            callback.call(false);
        });
    }

    public void postJournalEntry(Post journalEntry, AsyncUtils.ItemCallback<Post> callback) {
        PostConfig config = new PostConfig(journalEntry);
        postJournalEntry(config, callback);
    }

    public void postJournalEntry(PostConfig config, AsyncUtils.ItemCallback<Post> callback) {
        config.post.savePost(config, (post) -> relJournal.add(config.post, callback));
    }

    public void getFriendsList(AsyncUtils.ListCallback<User> callback){
        ParseQuery<User> pqUser = this.relFriends.getQuery();
        pqUser.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                callback.call(objects);
            }
        });
    }

    public void updateRecurrences() {
        updateRecurrences((e) -> {});
    }

    public void updateRecurrences(AsyncUtils.ItemCallback<Throwable> callback) {
        /* TODO:
         * - Loop through user's goals
         * - For each goal, loop through all actions
         * - For each action, update recurrences
         * - Migrate this logic to the User model
         */
        ParseQuery<Goal> goalQuery = relGoals.getQuery();
        goalQuery.findInBackground((goals, e) -> {
            if (e != null) {
                Log.e("User", "Failed to load goals while updating recurrences", e);
                return;
            }

            AsyncUtils.executeMany(
                goals.size(),
                (i, cb) -> {
                    // Get all actions on each goal for the current user
                    Goal g = goals.get(i);
                    ParseQuery<Action> actionQuery = g.relActions.getQuery();
                    actionQuery.whereEqualTo(Action.KEY_PARENT_USER, this);
                    actionQuery.include(Action.KEY_PARENT_SHARED_ACTION);

                    actionQuery.findInBackground((actions, e1) -> {
                        if (e1 != null) {
                            Log.e("User", "Failed to load actions while updating recurrences on Goal " + g.getTitle(), e);
                            cb.call(e1);
                            return;
                        }

                        // For each action, add all new recurs
                        AsyncUtils.executeMany(
                            (actions.size()),
                            (j, cb2) -> {
                                Action action = actions.get(j);
                                action.addRecur((recurs, e2) -> {
                                    cb2.call(e2);
                                });
                            },
                            (e2) -> {
                                cb.call(e2);
                            }
                        );
                    });
                },
                (e1) -> {
                    callback.call(e1);
                }
            );
        });
    }

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
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
