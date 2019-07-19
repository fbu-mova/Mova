package com.example.mova.feed;

public interface Priority<T> {
    /**
     * Calculates the priority of an item based on certain criteria.
     * @param item The item to prioritize.
     * @return The priority of the item.
     */
    float of(T item);
}
