package com.example.mova.model;


import android.util.Log;

import com.example.mova.utils.PostConfig;
import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
public class Post extends HashableParseObject {

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_GROUP = "group";
    public static final String KEY_IS_PERSONAL = "isPersonal";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_BODY = "body";
    public static final String KEY_MOOD = "mood";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_PARENT_POST = "parentPost";
    public static final String KEY_ID = "objectId";

    //Relations
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_TAGS = "tags";
    public final RelationFrame<Post> relComments = new RelationFrame<>(this, KEY_COMMENTS);
    public final RelationFrame<Tag> relTags = new RelationFrame<>(this, KEY_TAGS);

    //Author
    public User getAuthor(){
        return (User) getParseUser(KEY_AUTHOR);
    }

    public Post setAuthor(User user){
        put(KEY_AUTHOR, user);
        return this;
    }

    //Group
    public Group getGroup(){
        return (Group) getParseObject(KEY_GROUP);
    }

    public Post setGroup(Group group){
        put(KEY_GROUP, group);
        return this;
    }

    //isPersonal
    public boolean getIsPersonal(){
        return getBoolean(KEY_IS_PERSONAL);
    }

    public Post setIsPersonal(Boolean bool){
        put(KEY_IS_PERSONAL, bool);
        return this;
    }


    //location
    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public Post setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
        return this;
    }


    //body
    public String getBody() {
        return getString(KEY_BODY);
    }

    public Post setBody(String body) {
        put(KEY_BODY, body);
        return this;
    }

    //mood
    public Mood.Status getMood() {
        String moodValue = getString(KEY_MOOD);
        return (moodValue == null) ? Mood.Status.Empty : Mood.Status.valueOf(moodValue);
    }

    public Post setMood(Mood.Status mood) {
        put(KEY_MOOD, mood.toString());
        return this;
    }

    public Post removeMood() {
        put(KEY_MOOD, JSONObject.NULL);
        return this;
    }

    // Media
    public Media getMedia() {
        return (Media) getParseObject(KEY_MEDIA);
    }

    public Post setMedia(Media media) {
        put(KEY_MEDIA, media);
        return this;
    }

    public Post removeMedia() {
        put(KEY_MEDIA, JSONObject.NULL);
        return this;
    }

    // Parent post
    public Post getParent() {
        return (Post) getParseObject(KEY_PARENT_POST);
    }

    public Post setParent(Post parent) {
        put(KEY_PARENT_POST, parent);
        return this;
    }

    public Post removeParent() {
        put(KEY_PARENT_POST, JSONObject.NULL);
        return this;
    }

    // Saving posts
    public void savePost(AsyncUtils.ItemCallback<Post> callback) {
        savePost(new PostConfig(this), callback);
    }

    public void savePost(PostConfig config, AsyncUtils.ItemCallback<Post> callback) {
        Tag.saveTags(
            config.tags,
            (tag, cb) -> this.relTags.add(tag, (sameTag) -> cb.call()),
            (err) -> {
                if (err != null) {
                    Log.e("Post", "Failed to save post", err);
                    return;
                }

                AsyncUtils.ItemCallback<AsyncUtils.ItemCallback<Throwable>> saveAsReply = (cb) -> {
                    // Save as reply if reply
                    if (config.postToReply != null) {
                        this.setParent(config.postToReply);
                        config.postToReply.relComments.add(this);
                        config.postToReply.saveInBackground((e) -> {
                            if (e != null) {
                                Log.e("Post", "Failed to save postToReply", e);
                                cb.call(e);
                            } else {
                                cb.call(null);
                            }
                        });
                    } else {
                        cb.call(null);
                    }
                };

                AsyncUtils.ItemCallback<AsyncUtils.ItemCallback<Throwable>> saveInJournal = (cb) -> {
                    if (config.isPersonal && config.postToReply == null) {
                        User.getCurrentUser().relJournal.add(config.post, (saved) -> {
                            cb.call(null);
                        });
                    } else {
                        cb.call(null);
                    }
                };

                AsyncUtils.ItemCallback<AsyncUtils.ItemCallback<Throwable>> saveToGroup = (cb) -> {
                    if (config.post.getGroup() != null) {
                        config.post.getGroup().fetchIfNeededInBackground((obj, e) -> {
                            if (e != null) {
                                cb.call(e);
                                return;
                            }
                            Group group = (Group) obj;
                            group.relPosts.add(this, (saved) -> {
                                cb.call(null);
                            });
                        });
                    } else {
                        cb.call(null);
                    }
                };

                AsyncUtils.ItemCallback<AsyncUtils.ItemCallback<Throwable>> saveToEvent = (cb) -> {
                    if (config.event != null) {
                        config.event.fetchIfNeededInBackground((obj, e) -> {
                            if (e != null) {
                                cb.call(e);
                                return;
                            }
                            Event event = (Event) obj;
                            event.relComments.add(this, (saved) -> {
                                cb.call(null);
                            });
                        });
                    } else {
                        cb.call(null);
                    }
                };

                // TODO: If we ever add commenting on social goals, we'd do it with this.
//                AsyncUtils.ItemCallback<AsyncUtils.ItemCallback<Throwable>> saveToSocialGoal = (cb) -> {
//                    if (config.socialGoal != null) {
//                        config.socialGoal.fetchIfNeededInBackground((obj, e) -> {
//                            if (e != null) {
//                                cb.call(null);
//                                return;
//                            }
//                            Goal socialGoal = (Goal) obj;
//                            if (socialGoal.getIsPersonal()) {
//                                cb.call(null);
//                                return;
//                            }
//                        });
//                    } else {
//                        cb.call(null);
//                    }
//                };

                AsyncUtils.EmptyCallback doSavePost = () -> this.saveInBackground((e) -> {
                    if (e != null) {
                        Log.e("User", "Failed to save entry", e);
                    } else {
                        List<AsyncUtils.ExecuteManyCallback> actions = new ArrayList<>();

                        actions.add((i, cb) -> saveAsReply.call(cb));
                        actions.add((i, cb) -> saveInJournal.call(cb));
                        actions.add((i, cb) -> saveToGroup.call(cb));
                        actions.add((i, cb) -> saveToEvent.call(cb));

                        AsyncUtils.waterfall(actions, (e1) -> {
                            if (e1 != null) return;
                            callback.call(this);
                        });

//                        saveAsReply.call((e1) -> {
//                            if (e1 != null) return;
//                            saveInJournal.call((e2) -> {
//                                if (e2 != null) return;
//                                callback.call(this);
//                            });
//                        });
                    }
                });

                // Save media if it exists
                if (config.media != null) {
                    config.media.setParent(this);
                    config.media.saveInBackground((e) -> {
                        if (e != null) {
                            Log.e("User", "Failed to create media", e);
                        } else {
                            this.setMedia(config.media);
                            doSavePost.call();
                        }
                    });
                } else {
                    doSavePost.call();
                }
            }
        );
    }

    // Querying
    public static void includeAllPointers(ParseQuery<Post> query) {
        query.include(Post.KEY_AUTHOR);
        query.include(Post.KEY_GROUP);
        query.include(Post.KEY_MEDIA);
        query.include(Post.KEY_PARENT_POST);
        query.include(Post.KEY_LOCATION);
    }
}
