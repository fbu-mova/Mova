package com.example.mova.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.components.Component;

import java.util.List;

public class ComponentAdapter extends RecyclerView.Adapter<Component.ViewHolder> {
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
        component.makeViewHolder(activity, parent, false);
        return component.getViewHolder();
    }

    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        Component component = components.get(position);
        component.render();
    }

    @Override
    public int getItemCount() {
        return components.size();
    }
}
