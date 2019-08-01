package com.example.mova.model;

import android.util.Log;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class RelationFrame<T extends ParseObject> {

    private ParseObject parseObject;
    private String key;

    public RelationFrame(ParseObject parseObject, String key) {
        this.parseObject = parseObject;
        this.key = key;
    }
    public ParseRelation<T> getRelation(){
        return parseObject.getRelation(key);
    }

    public ParseQuery<T> getQuery() {
        ParseRelation<T> rel = parseObject.getRelation(key);
        return rel.getQuery();
    }

    public void getList(AsyncUtils.ListCallback<T> callback) {
        ParseQuery<T> query = getQuery();
        query.findInBackground((List<T> objects, ParseException e) -> {
            if (e != null){
                Log.e("RelationFrame", "Error retrieving list", e);
            } else {
                callback.call(objects);
            }
        });
    }

    public void getSize(AsyncUtils.ItemCallback<Integer> callback) {
        getList((objects) -> {
            callback.call(objects.size());
        });
    }

    // only adds the object to the relation w/o saving in background
    public void add(T object) {
        ParseRelation<T> relation = parseObject.getRelation(key);
        relation.add(object);
    }

    public void add(T object, AsyncUtils.ItemCallback<T> callback) {
        ParseRelation<T> relation = parseObject.getRelation(key);
        relation.add(object);
        parseObject.saveInBackground((ParseException e) -> {
            if (e != null) {
                Log.e("RelationFrame", "Error saving object", e);
            } else {
                callback.call(object);
            }
        });
    }

    public T remove(T object, AsyncUtils.EmptyCallback callback) {
        ParseRelation<T> relation = parseObject.getRelation(key);
        relation.remove(object);
        parseObject.saveInBackground((ParseException e) -> {
            if (e != null) {
                Log.e("RelationFrame", "Error deleting object", e);
            } else {
                callback.call();
            }
        });
        return object;
    }
}
