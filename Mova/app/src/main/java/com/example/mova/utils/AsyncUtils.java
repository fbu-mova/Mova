package com.example.mova.utils;

import android.util.Log;

import org.parceler.Parcel;

import java.util.List;

public class AsyncUtils {
    @Parcel
    public interface EmptyCallback {
        void call();
    }

    @Parcel
    public interface ItemCallback<T> {
        void call(T item);
    }

    @Parcel
    public interface ListCallback<T> extends ItemCallback<List<T>> { }

    @Parcel
    public interface ItemCallbackWithEmptyCallback<T> {
        void call(T item, EmptyCallback callback);
    }

    @Parcel
    public interface ItemCallbackWithItemCallback<T, Callback extends ItemCallback> {
        void call(T item, Callback callback);
    }

    @Parcel
    public interface ItemReturnCallback<T, R> {
        R call(T item);
    }

    private static class ExecuteManyStatus {
        public int count = 0;
        public boolean ranCallback = false;
    }

    public static void executeMany(int count, ItemCallbackWithItemCallback<Integer, ItemCallback<Throwable>> execute, EmptyCallback callback) {
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
}
