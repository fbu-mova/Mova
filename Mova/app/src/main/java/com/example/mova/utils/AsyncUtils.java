package com.example.mova.utils;

import android.util.Log;

import com.example.mova.model.Tag;

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

    private static class ExecuteAllStatus {
        public int count = 0;
        public boolean ranCallback = false;
    }

    public static <T> void executeAll(List<T> items, ItemCallbackWithItemCallback<T, ItemCallback<Throwable>> execute, EmptyCallback callback) {
        ExecuteAllStatus status = new ExecuteAllStatus();
        for (T item : items) {
            execute.call(item, (e) -> {
                if (e != null) {
                    Log.e("AsyncUtils", "Failed to execute call", e);
                } else {
                    status.count++;
                    if (!status.ranCallback && status.count == items.size()) {
                        callback.call();
                    }
                }
            });
        }
    }
}
