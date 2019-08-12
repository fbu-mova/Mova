package com.example.mova.utils;

/**
 * Stores one item. Intended for use in arrow function contexts, where an item must be final to be referenced in an internal closure (but this item doesn't have to be final).
 * @param <T> The type of item to store.
 */
public class Wrapper<T> {
    public T item;

    public Wrapper() {}

    public Wrapper(T item) {
        this.item = item;
    }
}
