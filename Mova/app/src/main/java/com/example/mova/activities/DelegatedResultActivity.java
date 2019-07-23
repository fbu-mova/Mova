package com.example.mova.activities;

import android.content.Intent;
import android.util.SparseArray;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An activity capable of delegating its intent result handling to a given callback.
 */
public abstract class DelegatedResultActivity extends AppCompatActivity {
    private SparseArray<ActivityResultCallback> delegatedCallbacks = new SparseArray<>();

    public interface ActivityResultCallback {
        void call(int requestCode, int resultCode, Intent data);
    }

    /**
     * Launches an intent on the current activity, and runs the given callback on activity result.
     * @param intent The intent to launch.
     * @param requestCode The requestCode for the result.
     * @param callback The callback to call upon retrieval of the intent's result.
     */
    public void startActivityForDelegatedResult(Intent intent, int requestCode, ActivityResultCallback callback) {
        delegatedCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultCallback callback = delegatedCallbacks.get(requestCode);
        if (callback != null) {
            delegatedCallbacks.remove(requestCode);
            callback.call(requestCode, resultCode, data);
        }
    }
}
