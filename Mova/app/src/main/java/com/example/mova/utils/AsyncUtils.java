package com.example.mova.utils;

import android.util.Log;

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

    private static class ExecuteManyStatus {
        public int count = 0;
        public boolean ranCallback = false;
    }

    public static void executeMany(int length, ItemCallbackWithItemCallback<Integer, ItemCallback<Throwable>> execute, EmptyCallback callback) {
        ExecuteManyStatus status = new ExecuteManyStatus();
        for (int i = 0; i < length; i++) {
            execute.call(i, (e) -> {
                if (e != null) {
                    Log.e("AsyncUtils", "Failed to execute call", e);
                } else {
                    status.count++;
                    if (!status.ranCallback && status.count == length) {
                        callback.call();
                    }
                }
            });
        }
    }
}
