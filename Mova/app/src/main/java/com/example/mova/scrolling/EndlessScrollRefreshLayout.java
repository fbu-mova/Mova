package com.example.mova.scrolling;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mova.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EndlessScrollRefreshLayout<VH extends RecyclerView.ViewHolder> extends FrameLayout {

    protected RecyclerView.Adapter<VH> adapter;
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected Handler handler;

    @BindView(R.id.rvItems)         protected RecyclerView rvItems;
    @BindView(R.id.swipeContainer)  protected SwipeRefreshLayout swipeContainer;

    public EndlessScrollRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public EndlessScrollRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EndlessScrollRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(LayoutConfig config, Handler<VH> handler) {
        // Determine whether to use wrap_constraint or match_parent layouts
        // FIXME: wrap_content doesn't work at all, and not because of the if statement
        int layoutId = makeLayout(config);
        inflate(getContext(), layoutId, this);
        ButterKnife.bind(this, this);

        this.handler = handler;

        // Configure RecyclerView
        RecyclerView.LayoutManager layoutManager = handler.getLayoutManager();
        rvItems.setLayoutManager(layoutManager);

        adapter = handler.getAdapter();
        rvItems.setAdapter(adapter);

        // Configure infinite scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                handler.loadMore();
            }
        };

        // Add the scroll listener to RecyclerView
        rvItems.addOnScrollListener(scrollListener);

        // Add swipe to refresh actions
        swipeContainer.setOnRefreshListener(() -> {
            handler.load();
            adapter.notifyDataSetChanged();
        });
        swipeContainer.setColorSchemeResources(handler.getColorScheme());
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        rvItems.addItemDecoration(decoration);
    }

    public void setRefreshing(boolean refreshing) {
        swipeContainer.setRefreshing(refreshing);
    }

    private int makeLayout(LayoutConfig config) {
        int layoutId;

        boolean xmp = config.widthSize == LayoutConfig.Size.match_parent;
        boolean ymp = config.heightSize == LayoutConfig.Size.match_parent;

        if (config.orientation == LayoutConfig.Orientation.Vertical) {
            if      (xmp && ymp)  layoutId = R.layout.layout_esrl_v_xmp_ymp;
            else if (xmp && !ymp) layoutId = R.layout.layout_esrl_v_xmp_ywc;
            else if (!xmp && ymp) layoutId = R.layout.layout_esrl_v_xwc_ymp;
            else /* xwc && ywc */ layoutId = R.layout.layout_esrl_v_xwc_ywc;
        } else { // Orientation.Horizontal
            if      (xmp && ymp)  layoutId = R.layout.layout_esrl_h_xmp_ymp;
            else if (xmp && !ymp) layoutId = R.layout.layout_esrl_h_xmp_ywc;
            else if (!xmp && ymp) layoutId = R.layout.layout_esrl_h_xwc_ymp;
            else /* xwc && ywc */ layoutId = R.layout.layout_esrl_h_xwc_ywc;
        }

        return layoutId;
    }

    public static int[] getDefaultColorScheme() {
        return new int[] {
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        };
    }

    public static abstract class Handler<VH extends RecyclerView.ViewHolder> {
        public abstract void load();
        public abstract void loadMore();

        public abstract RecyclerView.Adapter<VH> getAdapter();
        public abstract RecyclerView.LayoutManager getLayoutManager();

        public abstract int[] getColorScheme();
    }

    public static class LayoutConfig {
        public Orientation orientation;
        public Size widthSize;
        public Size heightSize;

        public LayoutConfig() {
            orientation = Orientation.Vertical;
            widthSize = Size.match_parent;
            heightSize = Size.match_parent;
        }

        public LayoutConfig setOrientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public LayoutConfig setWidthSize(Size widthSize) {
            this.widthSize = widthSize;
            return this;
        }

        public LayoutConfig setHeightSize(Size heightSize) {
            this.heightSize = heightSize;
            return this;
        }

        public static enum Size {
            match_parent,
            wrap_content
        }

        public static enum Orientation {
            Vertical,
            Horizontal
        }
    }
}
