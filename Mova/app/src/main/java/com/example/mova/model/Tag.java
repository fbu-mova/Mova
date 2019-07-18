package com.example.mova.model;

import android.util.Log;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Tag")
public class Tag extends ParseObject {

    public static final String KEY_NAME = "name";

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
}
