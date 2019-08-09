package com.example.mova.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class ViewAdapter<T extends View> extends RecyclerView.Adapter<ViewAdapter.ViewHolder<T>> {
    private Activity activity;
    private List<T> views;

    public ViewAdapter(Activity activity, List<T> views) {
        this.activity = activity;
        this.views = views;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // INVARIANT: viewType is the position in the list.
    @NonNull
    @Override
    public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parent.addView(views.get(viewType));
        return new ViewHolder<>(views.get(viewType));
    }

    @Override
    public int getItemCount() {
        return views.size();
    }

    public static class ViewHolder<T extends View> extends RecyclerView.ViewHolder {

        public final T view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = (T) itemView;
        }
    }
}
