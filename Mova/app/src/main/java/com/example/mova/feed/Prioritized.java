package com.example.mova.feed;

public class Prioritized<T> {
    public final T item;
    public final float value;

    public Prioritized(T item, float value) {
        this.item = item;
        this.value = value;
    }
}
