package com.example.mova.feed;

import java.util.ArrayList;
import java.util.List;

public abstract class Prioritizer<T> {
    protected List<Priority> priorities;

    public Prioritizer() {
        priorities = new ArrayList<>();
    }

    public void addPriority(Priority<T> priority) {
        priorities.add(priority);
    }

    public void addPriorities(Priority<T>... priorities) {
        for (Priority<T> priority : priorities) {
            addPriority(priority);
        }
    }

    public abstract float priorityOf(T item);
}
