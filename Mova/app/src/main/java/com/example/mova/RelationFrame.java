package com.example.mova;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class RelationFrame {

    private ParseObject parseObject;

    public RelationFrame(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    public ParseQuery getQuery(String key){
        return parseObject.getRelation(key).getQuery();
    }

    public List getList(String key){
        ParseQuery query = getQuery(key);
        List list = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null){
                    Log.e("Group", "error retrieving user list");
                }
                list.addAll(objects);
            }
        });
        return list;
    }

    public ParseObject add(String key, ParseObject object){
        ParseRelation relation = parseObject.getRelation(key);
        relation.add(object);
        parseObject.put(key, object);
        parseObject.saveInBackground();
        return parseObject;
    }

    public ParseObject remove(String key, ParseObject object){
        ParseRelation relation = parseObject.getRelation(key);
        relation.remove(object);
        parseObject.put(key, object);
        parseObject.saveInBackground();
        return parseObject;
    }
}
