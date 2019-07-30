package com.example.mova.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;

import java.util.List;

public abstract class ComponentAdapter extends RecyclerView.Adapter<Component.ViewHolder> {
    private DelegatedResultActivity activity;
    private List<Component> components;

    public ComponentAdapter(DelegatedResultActivity activity, List<Component> components) {
        this.activity = activity;
        this.components = components;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // INVARIANT: viewType is the position in the list.
    @NonNull
    @Override
    public Component.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Component component = components.get(viewType);
        Component.Inflater inflater = makeInflater(component);
        return inflater.inflate(activity, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        Component component = components.get(position);
        component.render(holder);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public void changeSource(List<Component> newSource) {
        this.components = newSource;
        notifyDataSetChanged();
    }

    protected abstract Component.Inflater makeInflater(Component component);
}
