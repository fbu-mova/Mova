package com.example.mova.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.components.Component;
import com.example.mova.feed.Prioritized;
import com.example.mova.feed.PrioritizedComponent;

public class PrioritizedComponentAdapter extends RecyclerView.Adapter<Component.ViewHolder> {
    private DelegatedResultActivity activity;
    private SortedList<PrioritizedComponent> components;

    public PrioritizedComponentAdapter(DelegatedResultActivity activity, SortedList<PrioritizedComponent> components) {
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
        Prioritized<Component> pComponent = components.get(viewType);
        pComponent.item.makeViewHolder(activity, parent, false);
        return pComponent.item.getViewHolder();
    }

    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        Prioritized<Component> pComponent = components.get(position);
        pComponent.item.render();
    }

    @Override
    public int getItemCount() {
        return components.size();
    }
}
