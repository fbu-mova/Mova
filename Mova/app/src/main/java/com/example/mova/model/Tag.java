package com.example.mova.model;

import android.util.Log;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Tag")
public class Tag extends HashableParseObject {

    public static final String KEY_NAME = "name";

    //Relations
    public static final String KEY_GOALS = "goals";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_GROUPS = "groups";
    public static final String KEY_POSTS = "posts";
    public final RelationFrame<Goal> relGoals = new RelationFrame<>(this, KEY_GOALS);
    public final RelationFrame<Event> relEvents = new RelationFrame<>(this, KEY_EVENTS);
    public final RelationFrame<Group> relGroups = new RelationFrame<>(this, KEY_GROUPS);
    public final RelationFrame<Post> relPosts = new RelationFrame<>(this, KEY_POSTS);

    public Tag(){}

    public Tag(String name){
        this.setName(name);
    }

    public String getName(){
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    /**
     * Queries the database for a tag with the given name.
     * Returns null if no such tag exists.
     * @param name The name of the tag.
     * @param callback The callback to call after fetching the tag.
     */
    public static void getTag(String name, AsyncUtils.ItemCallback<Tag> callback) {
        ParseQuery<Tag> query = new ParseQuery<>(Tag.class);
        query.whereEqualTo(KEY_NAME, name);
        query.findInBackground((List<Tag> objects, ParseException e) -> {
            if (e != null) {
                Log.e("Tag", "Failed to load tags for exists query", e);
                callback.call(null);
            } else {
                callback.call((objects.size() == 0) ? null : objects.get(0));
            }
        });
    }

    public static void saveTags(List<Tag> tags, AsyncUtils.ItemCallbackWithEmptyCallback<Tag> onSaveTag, AsyncUtils.ItemCallback<Throwable> callback) {
        AsyncUtils.executeMany(
            tags.size(),
            (position, cb) -> {
                Tag tag = tags.get(position);
                Tag.getTag(tag.getName(), (tagFromDB) -> {
                    // If the tag doesn't exist yet, save it
                    if (tagFromDB == null) {
                        tag.saveInBackground((e) -> {
                            if (e != null) {
                                Log.e("User", "Failed to create tag " + tag.getName() + " on journal post", e);
                                cb.call(e);
                            } else {
                                onSaveTag.call(tag, () -> cb.call(null));
                            }
                        });
                    } else {
                        onSaveTag.call(tagFromDB, () -> cb.call(null));
                    }
                });
            },
            callback
        );
    }
}
