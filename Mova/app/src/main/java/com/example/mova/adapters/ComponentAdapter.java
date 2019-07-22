package com.example.mova.adapters;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.components.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Adapts items to a list of components, which are then displayed.
 * @param <T> The type of item to use for each component.
 */
public abstract class ComponentAdapter<T> extends RecyclerView.Adapter<Component.ViewHolder> {
    private Activity activity;
    private List<T> items;
    private HashMap<T, Component<T>> components;

    public ComponentAdapter(Activity activity, List<T> items) {
        this.activity = activity;
        this.items = items;

        // added
        this.components = new HashMap<>();
    }

    /**
     * Serves as a factory for the type of component that should be used.
     * @param item The item to use as data for the component.
     * @return The component to display.
     */
    public abstract Component<T> makeComponent(T item);

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
        Component<T> component = makeComponent(item);
        components.put(item, component); // FIXME: Make sure that this overrides the last value
        component.makeViewHolder(activity, parent);
        return component.getViewHolder();
    }

    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        T item = items.get(position); // items has size 14
        Component<T> component = components.get(item); // components has size 13
        if (component == null) { // fixme -- jank solution to onCreateViewHolder not being called before this
            // need to make a component and put it into components
            component = makeComponent(item);
            components.put(item, component);
        }
        component.render(); // component is null
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
