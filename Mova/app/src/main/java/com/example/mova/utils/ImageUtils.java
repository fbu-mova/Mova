package com.example.mova.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ImageUtils {

    public static final String TAG = "ImageUtils";
    public static final String photoFileName = "photo.jpg";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int PICK_PHOTO_CODE = 1046;

    // choose from gallery
    public static void chooseFromGallery(Activity activity) {

        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            activity.startActivityForResult(intent, PICK_PHOTO_CODE);
        }

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

    // fixme -- slightly sketch workarounds
    // handles ActivityForResult from either gallery or camera -- true means result WASN'T them
    public static boolean onImageActivityResult(int requestCode, int resultCode, @Nullable Intent data,
                                                Activity activity, ImageView imageView,
                                                File photoFile, ParseFile parseFile) {

        if (resultCode == RESULT_OK) {

            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                // by this point we have the camera photo on disk

                // Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // RESIZE BITMAP, see section below
                Uri takenPhotoUri = Uri.fromFile(photoFile);
                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 200);

                // Load the taken image into a preview
                imageView.setImageBitmap(resizedBitmap);
            }

            else if (requestCode == PICK_PHOTO_CODE) {

                if (data != null) {
                    Uri photoUri = data.getData();
                    // Do something with the photo based on Uri
                    Bitmap selectedImage = null;
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), photoUri);
                        bitmapToParse(selectedImage, parseFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Load the selected image into a preview
                    imageView.setImageBitmap(selectedImage);
                }
            }

        }

        return true;
    }

    // save image to Parse as ParseFile
    public static void saveToParse(ParseFile file, ParseObject parentObject, String imageKey,
                                   AsyncUtils.ItemCallback<ParseFile> callback) {
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

    // convert image of type Bitmap to ParseFile
    private static void bitmapToParse(Bitmap selectedImage, ParseFile parseFile) {
        // used code from: https://stackoverflow.com/questions/31227547/how-to-upload-image-to-parse-in-android
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.androidbegin);
        // Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        // Create the ParseFile
        parseFile = new ParseFile("from_gallery.jpg", image);
//                // Upload the image into Parse Cloud
//                file.saveInBackground();
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

    // resize image to fit Parse more; may not use
    public static void resizeImage() {

    }

}
