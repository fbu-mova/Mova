package com.example.mova;

import android.util.Log;

import com.example.mova.utils.AsyncUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class RelationFrame<T extends ParseObject> {

    private ParseObject parseObject;

    public RelationFrame(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public ParseQuery<T> getQuery(String key) {
        ParseRelation<T> rel = parseObject.getRelation(key);
        return rel.getQuery();
    }

    public void getList(String key, AsyncUtils.ListCallback<T> callback) {
        ParseQuery<T> query = getQuery(key);
        query.findInBackground((List<T> objects, ParseException e) -> {
            if (e != null){
                Log.e("RelationFrame", "Error retrieving list", e);
            } else {
                callback.call(objects);
            }
        });
    }

    public void add(String key, T object, AsyncUtils.ItemCallback<T> callback) {
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

    public T remove(String key, T object, AsyncUtils.EmptyCallback callback) {
        ParseRelation<T> relation = parseObject.getRelation(key);
        relation.remove(object);
        parseObject.deleteInBackground((ParseException e) -> {
            if (e != null) {
                Log.e("RelationFrame", "Error deleting object", e);
            } else {
                callback.call();
            }
        });
        return object;
    }
}
