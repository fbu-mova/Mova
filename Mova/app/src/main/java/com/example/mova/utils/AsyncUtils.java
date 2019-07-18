package com.example.mova.utils;

import java.util.List;

public interface AsyncUtils {
    interface EmptyCallback {
        void call();
    }

    interface ItemCallback<T> {
        void call(T item);
    }

    interface ListCallback<T> {
        void call(List<T> items);
    }
}
