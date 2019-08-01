package com.example.mova.utils;

import java.util.ArrayList;
import java.util.List;

public class DataEvent<T> {
    protected List<AsyncUtils.ItemCallback<T>> listeners;

    public DataEvent() {
        listeners = new ArrayList<>();
    }

    public void addListener(AsyncUtils.ItemCallback<T> listener) {
        listeners.add(listener);
    }

    public void fire(T data) {
        for (AsyncUtils.ItemCallback<T> l : listeners) {
            l.call(data);
        }
    }
}
