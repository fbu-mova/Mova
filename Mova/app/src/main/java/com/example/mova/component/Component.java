package com.example.mova.component;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.activities.DelegatedResultActivity;

/**
 * Bundles a view's logic, state, and binding.
 */
public abstract class Component {

    /**
     * Inflates the component's layout into the given activity.
     * @param activity The Activity into which to inflate the component.
     * @param parent The ViewGroup into which to inflate the component.
     */
    public abstract void makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot);

    /**
     * Returns the ViewHolder that the component has created once inflated.
     * @return The ViewHolder. Null if not yet inflated.
     */
    public abstract ViewHolder getViewHolder();

    /**
     * Returns the View created when making the ViewHolder.
     * @return The View. Null if not yet inflated.
     */
    public abstract View getView();

    /**
     * Returns a string representation of the component.
     * Intended for use in ComponentManager and other component keying systems.
     * @return A string representation of the component.
     */
    public abstract String getName();

    /**
     * Sets the manager of this component to enable component swapping.
     */
    public abstract void setManager(ComponentManager manager);

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
