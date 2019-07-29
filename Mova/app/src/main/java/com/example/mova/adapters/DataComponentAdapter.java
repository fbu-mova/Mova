package com.example.mova.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.components.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Adapts items to a list of components, which are then displayed.
 * @param <T> The type of item to use for each component.
 */
public abstract class DataComponentAdapter<T> extends RecyclerView.Adapter<Component.ViewHolder> {
    private DelegatedResultActivity activity;
    private List<T> items;
    private HashMap<T, Component> components;

    public DataComponentAdapter(DelegatedResultActivity activity, List<T> items) {
        this.activity = activity;
        this.items = items; // TODO: If null, instantiate
        this.components = new HashMap<>();
    }

    /**
     * Serves as a factory for the type of component that should be used.
     * @param item The item to use as data for the component.
     * @return The component to display.
     */
    public abstract Component makeComponent(T item);

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // INVARIANT: viewType is the position in the list.
    // CONSTRAINT: Items in items must be unique (and uniquely identifiable by .equals()).
    @NonNull
    @Override
    public Component.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        T item = items.get(viewType);
        Component component = makeComponent(item);
        components.put(item, component); // FIXME: Make sure that this overrides the last value
        component.makeViewHolder(activity, parent, false);
        return component.getViewHolder();
    }

    // FIXME: Is holder null when bind is called before create?
    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        T item = items.get(position);
        Component component = components.get(item); // FIXME: Refactor--create a component constructor that takes in a ViewHolder, and make the creation of ViewHolders static instead
        component.render();
    }

    @Override
    public int getItemCount() {
        if (items == null) { // FIXME: When is this used?
            return 0;
        }
        return items.size();
    }

    /**
     * Changes the source of the data. Does not update the RecyclerView.
     * To be effective, requires that the RecyclerView's adapter be reattached (rv.setAdapter(this)),
     * and that the adapter only then be notified that the entire data set has changed.
     * @param newSource The new source to use.
     */
    public void changeSource(List<T> newSource) {
        this.items = newSource;
    }
}
