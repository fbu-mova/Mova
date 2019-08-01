package com.example.mova.utils;

import java.util.ArrayList;
import java.util.List;

public class Event {
    protected List<AsyncUtils.EmptyCallback> listeners;

    public Event() {
        listeners = new ArrayList<>(listeners);
    }

    public void addListener(AsyncUtils.EmptyCallback listener) {
        listeners.add(listener);
    }

    public void fire() {
        for (AsyncUtils.EmptyCallback l : listeners) {
            l.call();
        }
    }
}
