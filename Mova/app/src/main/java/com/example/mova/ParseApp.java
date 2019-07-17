package com.example.mova;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu_mova")
                .clientKey("very_secret")
                .server("https://fbu-mova.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

    }
}
