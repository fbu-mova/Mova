package com.example.mova;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class RelationFrame extends ParseObject {


    public ParseQuery getQuery(String key){
        return getRelation(key).getQuery();
    }

    public List getList(String key){
        ParseQuery query = getQuery(key);
        List list = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null){
                    Log.e("Group", "error retriving user list");
                }
                list.addAll(objects);
            }
        });
        return list;
    }

    public ParseObject add(String key, ParseObject object){
        ParseRelation relation = getRelation(key);
        relation.add(object);
        put(key, object);
        saveInBackground();
        return this;
    }

    public ParseObject remove(String key, ParseObject object){
        ParseRelation relation = getRelation(key);
        relation.remove(object);
        put(key, object);
        saveInBackground();
        return this;
    }
}
