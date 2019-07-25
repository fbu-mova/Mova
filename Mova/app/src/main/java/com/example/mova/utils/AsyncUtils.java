package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.RelationFrame;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.List;

public class AsyncUtils {

    public static final String TAG = "AsyncUtils";

    public interface EmptyCallback {
        void call();
    }

    public interface ItemCallback<T> {
        void call(T item);
    }

    public interface ListCallback<T> extends ItemCallback<List<T>> { }

    public interface ItemCallbackWithEmptyCallback<T> {
        void call(T item, EmptyCallback callback);
    }

    public interface ItemCallbackWithItemCallback<T, Callback extends ItemCallback> {
        void call(T item, Callback callback);
    }

    public interface ItemReturnCallback<T, R> {
        R call(T item);
    }

    private static class ExecuteManyStatus {
        public int count = 0;
        public boolean ranCallback = false;
    }

    // (int position, (Throwable e) -> void) -> void
    public interface ExecuteManyCallback extends ItemCallbackWithItemCallback<Integer, ItemCallback<Throwable>> { }

    /**
     * Executes multiple asynchronous actions, and calls the callback only once all actions have been completed.
     * @param count The number of asynchronous actions to execute.
     * @param execute The action to execute for each index. Must call the provided callback once the action is complete.
     * @param callback The callback to call once all actions have finished.
     */
    public static void executeMany(int count, ExecuteManyCallback execute, ItemCallback<Throwable> callback) {
        ExecuteManyStatus status = new ExecuteManyStatus();
        if (count == 0) {
            callback.call(null);
        }
        for (int i = 0; i < count; i++) {
            execute.call(i, (e) -> {
                if (e != null) {
                    Log.e("AsyncUtils", "Failed to execute call", e);
                } else {
                    status.count++;
                    if (!status.ranCallback && status.count == count) {
                        callback.call(e);
                    }
                }
            });
        }
    }

    /**
     * Executes multiple asynchronous actions, and calls the callback only once all actions have been completed.
     * @param asyncActions The actions to execute. Each action must call the provided callback once the action is complete.
     * @param callback The callback to call once all actions have finished.
     */
    public static void executeMany(List<ExecuteManyCallback> asyncActions, ItemCallback<Throwable> callback) {
        executeMany(asyncActions.size(), (i, cb) -> asyncActions.get(i).call(i, cb), callback);
    }

    public static void saveWithRelation(ParseObject object, RelationFrame relation, ItemCallback callback) {
        // saves the child automatically to the parent in corresponding parent relation
            // fixme -- currently can only add object one at a time to parent object. can't .add all, then save so only one save?

        // save object in background in own class (e.g. journal entry saved to Post class)
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "object saved in class successfully");

                    // save object in the relation of the other class through relation
                    // (e.g. journal entry saved to that user's journal relation...)
                    relation.add(object, new ItemCallback() {
                        @Override
                        public void call(Object item) {
                            // when complete, so assumes it completes successfully (?)
                            callback.call(object); // like this so coder has access to the callback rather than doubly nested
                        }
                    });
                }
                else {
                    Log.e(TAG, "object failed saving in class", e);
                }
            }
        });
    }

}
