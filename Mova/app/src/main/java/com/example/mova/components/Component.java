package com.example.mova.components;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.activities.DelegatedResultActivity;

/**
 * Handles the view for a given piece of information.
 * @param <T> The type of information.
 */
public abstract class Component<T> {
    protected T item;

    /**
     * Use this only if the component is not meant to have an item.
     */
    public Component() { }

    public Component(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    /**
     * Inflates the component's layout into the given activity.
     * @param activity The Activity into which to inflate the component.
     * @param parent The ViewGroup into which to inflate the component.
     */
    public abstract void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent);

    /**
     * Returns the ViewHolder that the component has created once inflated.
     * @return The ViewHolder. Null if not yet inflated.
     */
    public abstract ViewHolder getViewHolder();

    /**
     * Renders any relevant data or events to the component's ViewHolder.
     * Does nothing if not yet inflated.
     */
    public abstract void render();

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
