package com.example.mova;

import android.app.Application;

import com.example.mova.model.Action;
import com.example.mova.model.Event;
import com.example.mova.model.Goal;
import com.example.mova.model.Group;
import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.example.mova.model.SharedAction;
import com.example.mova.model.Tag;
import com.example.mova.model.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Registering all the subclasses
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Action.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Goal.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Media.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(SharedAction.class);
        ParseObject.registerSubclass(Tag.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu_mova")
                .clientKey("very_secret")
                .server("https://fbu-mova.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

    }
}
