package com.example.mova.feed;

public class Prioritized<T> implements Comparable<Prioritized<T>> {
    public final T item;
    public final float value;

    public Prioritized(T item, float value) {
        this.item = item;
        this.value = value;
    }


    @Override
    public int compareTo(Prioritized<T> o) {
        // FIXME: This very well could rely on the subtraction of the floats instead, but this works for now.
        if (value < o.value)       return -1;
        else if (value == o.value) return 0;
        return 1;
    }
}
