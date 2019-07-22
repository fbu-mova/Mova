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

    public static void executeMany(int count, ExecuteManyCallback execute, EmptyCallback callback) {
        ExecuteManyStatus status = new ExecuteManyStatus();
        if (count == 0) {
            callback.call();
        }
        for (int i = 0; i < count; i++) {
            execute.call(i, (e) -> {
                if (e != null) {
                    Log.e("AsyncUtils", "Failed to execute call", e);
                } else {
                    status.count++;
                    if (!status.ranCallback && status.count == count) {
                        callback.call();
                    }
                }
            });
        }
    }

    public static void executeMany(List<ExecuteManyCallback> execute, EmptyCallback callback) {
        executeMany(execute.size(), (i, cb) -> execute.get(i).call(i, cb), callback);
    }
}
