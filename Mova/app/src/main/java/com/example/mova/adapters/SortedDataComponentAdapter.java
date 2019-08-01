package com.example.mova.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;

import java.util.HashMap;

/**
 * Adapts items to a sorted list of components, which are then displayed.
 * @param <T> The type of item to use for each component.
 */
public abstract class SortedDataComponentAdapter<T> extends RecyclerView.Adapter<Component.ViewHolder> {
    private DelegatedResultActivity activity;
    private SortedList<T> items;
    private HashMap<T, Component> components;

    public SortedDataComponentAdapter(DelegatedResultActivity activity, SortedList<T> items) {
        this.activity = activity;
        this.items = items;
        this.components = new HashMap<>();
    }

    /**
     * Serves as a factory for the type of component that should be used.
     * @param item The item to use as data for the component.
     * @return The component to display.
     */
    protected abstract Component makeComponent(T item, Component.ViewHolder holder);

    /**
     * Serves as a factory for the type of component inflater that should be used.
     * @param item The item to use as data for the component inflater.
     * @return The inflater with which to inflate the component's view.
     */
    protected abstract Component.Inflater makeInflater(T item);

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
        Component.Inflater inflater = makeInflater(item);
        return inflater.inflate(activity, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        T item = items.get(position);
        Component component = components.get(item);
        if (component == null) component = makeComponent(item, holder);
        component.render(activity, holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Changes the source of the data. Does not update the RecyclerView.
     * To be effective, requires that the RecyclerView's adapter be reattached (rv.setAdapter(this)),
     * and that the adapter only then be notified that the entire data set has changed.
     * @param newSource The new source to use.
     */
    public void changeSource(SortedList<T> newSource) {
        this.items = newSource;
    }
}
