package com.example.mova.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.File;

public class ImageUtils {

    public static final String TAG = "ImageUtils";
    public static final String photoFileName = "photo.jpg";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    // choose from gallery
    public static void chooseFromGallery() {

    }

    // launch camera and choose from there; copied from Parstagram
    public static void launchCamera(File photoFile, ParseFile parseFile, Activity activity) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName, activity);
        parseFile = fileToParseFile(photoFile);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(activity, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // handles ActivityForResult from either gallery or camera -- true means result WASN'T them
    public static boolean onImageActivityResult() {
        return true;
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

    // same as previous but doesn't save yet if want to edit parentObject more before saving
    public static ParseObject saveToParse(ParseFile file, ParseObject parentObject, String imageKey) {
        parentObject.add(imageKey, file);
        return parentObject;
    }

    // convert image of type File to ParseFile
    public static ParseFile fileToParseFile(File photoFile) {
        return new ParseFile(photoFile);
    }

    // Returns the File for a photo stored on disk given the fileName
    private static File getPhotoFileUri(String photoFileName, Activity activity) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }


    // resize image to fit Parse more
    public static void resizeImage() {

    }

}
