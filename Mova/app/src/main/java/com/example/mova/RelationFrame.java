package com.example.mova;

import android.util.Log;

import com.example.mova.utils.AsyncUtils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class RelationFrame<T extends ParseObject> {

    private ParseObject parseObject;

    public RelationFrame(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public ParseQuery getQuery(String key){
        return parseObject.getRelation(key).getQuery();
    }

    public void getList(String key, AsyncUtils.ListCallback<T> callback){
        ParseQuery<T> query = getQuery(key);
        query.findInBackground((List<T> objects, ParseException e) -> {
            if (e != null){
                Log.e("RelationFrame", "Error retrieving list", e);
            } else {
                callback.call(objects);
            }
        });
    }

    public void add(String key, T object, AsyncUtils.ItemCallback<T> callback){
        ParseRelation<T> relation = parseObject.getRelation(key);
        relation.add(object);
        parseObject.put(key, object);
        parseObject.saveInBackground((ParseException e) -> {
            if (e != null) {
                Log.e("RelationFrame", "Error saving object", e);
            } else {
                callback.call(object);
            }
        });
    }

    public T remove(String key, T object, AsyncUtils.EmptyCallback callback){
        ParseRelation<T> relation = parseObject.getRelation(key);
        relation.remove(object);
        parseObject.put(key, object);
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
