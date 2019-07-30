package com.example.mova.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.component.Component;
import com.example.mova.feed.PrioritizedComponent;

public abstract class PrioritizedComponentAdapter extends RecyclerView.Adapter<Component.ViewHolder> {
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
        PrioritizedComponent pComponent = components.get(viewType);
        Component.Inflater inflater = makeInflater(pComponent);
        return inflater.inflate(activity, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull Component.ViewHolder holder, int position) {
        PrioritizedComponent pComponent = components.get(position);
        pComponent.item.render(activity, holder);
    }

    @Override
    public int getItemCount() {
        return components.size();
    }

    public void changeSource(SortedList<PrioritizedComponent> newSource) {
        this.components = newSource;
        notifyDataSetChanged();
    }

    protected abstract Component.Inflater makeInflater(PrioritizedComponent prioritizedComponent);
}
