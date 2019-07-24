package com.example.mova.utils;

import android.util.Log;

import org.parceler.Parcel;

import java.util.List;

public class AsyncUtils {
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
     * Executes multiple asynchronous actions in parallel, and calls the callback only once all actions have been completed.
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
     * Executes multiple asynchronous actions in parallel, and calls the callback only once all actions have been completed.
     * @param asyncActions The actions to execute. Each action must call the provided callback once the action is complete.
     * @param callback The callback to call once all actions have finished.
     */
    public static void executeMany(List<ExecuteManyCallback> asyncActions, ItemCallback<Throwable> callback) {
        executeMany(asyncActions.size(), (i, cb) -> asyncActions.get(i).call(i, cb), callback);
    }

    /**
     * Executes multiple asynchronous actions in series, and calls the callback only once all actions have been completed.
     * @param count The number of asynchronous actions to execute.
     * @param execute The action to execute for each index. Must call the provided callback once the action is complete.
     * @param callback The callback to call once all actions have finished.
     */
    public static void waterfall(int count, ExecuteManyCallback execute, ItemCallback<Throwable> callback) {
        execute.call(0, (e) -> waterfallCallNext(0, count, e, execute, callback));
    }

    /**
     * Executes multiple asynchronous actions in series, and calls the callback only once all actions have been completed.
     * @param asyncActions The actions to execute. Each action must call the provided callback once the action is complete.
     * @param callback The callback to call once all actions have finished.
     */
    public static void waterfall(List<ExecuteManyCallback> asyncActions, ItemCallback<Throwable> callback) {
        waterfall(asyncActions.size(), (i, cb) -> asyncActions.get(i).call(i, cb), callback);
    }

    /**
     * The mutually(-ish) recursive helper method for the next waterfall action.
     * @param i The current position, soon to be incremented.
     * @param count The total number of asynchronous actions.
     * @param err Any error that may have occurred during the asynchronous action, passed in from the callback given to the ExecuteManyCallback (asynchronous action).
     * @param execute The function to execute at each index (the asynchronous action).
     * @param finalCb The callback to call once all actions have finished.
     */
    private static void waterfallCallNext(int i, int count, Throwable err, ExecuteManyCallback execute, ItemCallback<Throwable> finalCb) {
        if (err != null) {
            finalCb.call(err);
        } else {
            i++;
            // If done, call the callback
            if (i == count) {
                finalCb.call(err);
            } else {
                // Otherwise, execute the next position
                final int iFinal = i;
                execute.call(i, (e) -> waterfallCallNext(iFinal, count, e, execute, finalCb));
            }
        }
    }
}
