package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Media;
import com.example.mova.model.Post;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class MediaUtils {

    public static final String TAG = "MediaUtils";

    /**
     * Creates and saves an instance of media to their corresponding post.
     * @param parentPost
     * @param child
     * @param callback
     */
    public static void updateMediaToPost(Post parentPost, Object child, AsyncUtils.ItemCallback<Object> callback) {

        // create Media
        Media childMedia = new Media()
                .setParent(parentPost)
                .setContent(child);

        // save childMedia
        childMedia.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "saved child media");
                    parentPost.setMedia(childMedia).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "saved child media to parent post");
                            }
                            else {
                                Log.e(TAG, "failed saving child media to parent post", e);
                            }
                            callback.call(parentPost);
                        }
                    });
                }
                else {
                    Log.e(TAG, "failed saving child media");
                    callback.call(parentPost);
                }
            }
        });
    }
}
