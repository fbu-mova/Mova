package com.example.mova.containers;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;

public abstract class ScrollLoadHandler<VH extends RecyclerView.ViewHolder> {

    public abstract void load();
    public abstract void loadMore();

    public abstract RecyclerView.Adapter<VH> getAdapter();
    public abstract RecyclerView.LayoutManager getLayoutManager();

    public abstract int[] getColorScheme();

    public static int[] getDefaultColorScheme() {
        return new int[] {
//                android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light
                R.color.purpleMid,
                R.color.orangeMid,
                R.color.blueMid,
        };
    }
}
