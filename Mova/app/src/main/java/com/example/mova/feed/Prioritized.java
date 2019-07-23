package com.example.mova.feed;

/**
 * An item with an associated integer value to signify its priority.
 * Items must have unique hash codes for proper comparison if items are equal.
 * @param <T> The type of item with which to associate a value.
 */
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
        if (value < o.value) {
            return -1;
        } else if (value > o.value) {
            return 1;
        }
        // If objects are equal, compare their hash codes
        // T should not have to implement Comparable<T>, since Prioritized<T> acts as the main comparison point
        return Integer.compare(item.hashCode(), o.item.hashCode());
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }
}
