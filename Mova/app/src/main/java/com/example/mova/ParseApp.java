package com.example.mova;

import android.app.Application;

import com.example.mova.model.Comment;
import com.example.mova.model.Group;
import com.example.mova.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Comment.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu_mova")
                .clientKey("very_secret")
                .server("https://fbu-mova.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

    }
}
