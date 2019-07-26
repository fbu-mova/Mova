package com.example.mova.utils;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class ImageUtils {

    public static final String TAG = "ImageUtils";

    // choose from gallery
    public static void chooseFromGallery() {

    }

    // launch camera and choose from there
    public static void launchCamera() {

    }

    // resize image to fit Parse more
    public static void resizeImage() {

    }

    // convert image of type ? to ParseFile
    public static void toParseFile() {

    }

    // save image to Parse as ParseFile
    public static void saveToParse(ParseFile file, ParseObject parentObject, String imageKey, AsyncUtils.ItemCallback<ParseFile> callback) {
        parentObject.add(imageKey, file);
        parentObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "saved image");
                    callback.call(file);
                }
                else {
                    Log.e(TAG, "image failed saving", e);
                }
            }
        });
    }
}
