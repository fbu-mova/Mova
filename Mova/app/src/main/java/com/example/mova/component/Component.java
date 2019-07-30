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
     * Returns the component's most recently used ViewHolder.
     * @return The ViewHolder.
     */
    public abstract ViewHolder getViewHolder();

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
     * @param holder The ViewHolder with which to render the component.
     */
    public abstract void render(ViewHolder holder);

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        /**
         * Returns the View created when making the ViewHolder.
         * @return The View. Null if not yet inflated.
         */
        public abstract View getView();
    }

    /**
     * Responsible for creating all elements necessary for the creation of a component.
     * If any logic is required to determine which kind of layout to use, etc., it should be done here.
     */
    public static abstract class Factory {
        /**
         * Inflates the component's layout into the given activity.
         * @param activity The Activity into which to inflate the component.
         * @param parent The ViewGroup into which to inflate the component.
         */
        public abstract ViewHolder makeViewHolder(DelegatedResultActivity activity, ViewGroup parent, boolean attachToRoot);
    }
}
