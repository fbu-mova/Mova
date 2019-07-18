package com.example.mova.components;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Handles the view for a given piece of information.
 * @param <T> The type of information.
 */
public abstract class Component<T> {
    protected T item;

    public Component(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public abstract void makeViewHolder(Activity activity, ViewGroup parent);

    public abstract ViewHolder getViewHolder();

    public abstract void bind();

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
