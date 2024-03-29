package com.example.mova.containers;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mova.R;
import com.example.mova.utils.AsyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EndlessScrollRefreshLayout<VH extends RecyclerView.ViewHolder> extends FrameLayout {

    protected RecyclerView.Adapter<VH> adapter;
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected LayoutConfig config;
    protected ScrollLoadHandler handler;

    @BindView(R.id.rvItems) protected RecyclerView rvItems;
    protected SwipeContainer swipeContainer;

    public EndlessScrollRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public EndlessScrollRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EndlessScrollRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(ScrollLoadHandler<VH> handler) {
        init(new LayoutConfig(), handler);
    }

    public void init(LayoutConfig config, ScrollLoadHandler<VH> handler) {
        int layoutId = makeLayout(config);
        inflate(getContext(), layoutId, this);

        swipeContainer = new SwipeContainer(R.id.swipeContainer, config.orientation);
        swipeContainer.bind();

        ButterKnife.bind(this, this);

        this.handler = handler;
        this.config = config;

        // Configure RecyclerView
        RecyclerView.LayoutManager layoutManager = handler.getLayoutManager();
        rvItems.setLayoutManager(layoutManager);

        adapter = handler.getAdapter();
        rvItems.setAdapter(adapter);

        // Configure infinite scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("ESRL", "Hit load more");
                // Triggered only when new data needs to be appended to the list
                handler.loadMore();
            }
        };

        // Add the scroll listener to RecyclerView
        rvItems.addOnScrollListener(scrollListener);

        // Add swipe to refresh actions
        swipeContainer.setOnRefreshListener(() -> {
            Log.d("ESRL", "Hit load");
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

    public void reattachAdapter() {
        rvItems.setAdapter(handler.getAdapter());
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        rvItems.setNestedScrollingEnabled(enabled);
    }

    public void scrollToPosition(int position) {
        rvItems.scrollToPosition(position);
    }

    private int makeLayout(LayoutConfig config) {
        // FIXME: wrap_content doesn't work at all, and not because of the if statement
        int layoutId;

        // Determine whether to use wrap_constraint or match_parent layouts
        boolean xmp = config.widthSize == LayoutConfig.Size.match_parent;
        boolean ymp = config.heightSize == LayoutConfig.Size.match_parent;

        // Determine whether to use vertical or horizontal pull to refresh
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

    protected class SwipeContainer {
        public final int viewId;
        public final LayoutConfig.Orientation orientation;

        public SwipeContainer(int viewId, LayoutConfig.Orientation orientation) {
            this.viewId = viewId;
            this.orientation = orientation;
        }

        public SwipeRefreshLayout vertical;
        public custom.widget.SwipeRefreshLayout horizontal;

        public void bind() {
            if (orientation == LayoutConfig.Orientation.Vertical) {
                vertical = EndlessScrollRefreshLayout.this.findViewById(viewId);
            } else {
                horizontal = EndlessScrollRefreshLayout.this.findViewById(viewId);
            }
        }

        public void setOnRefreshListener(AsyncUtils.EmptyCallback onRefreshListener) {
            if (orientation == LayoutConfig.Orientation.Vertical) {
                vertical.setOnRefreshListener(() -> onRefreshListener.call());
            } else {
                horizontal.setOnRefreshListener(() -> onRefreshListener.call());
            }
        }

        public void setColorSchemeResources(int... colorResIds) {
            if (orientation == LayoutConfig.Orientation.Vertical) {
                vertical.setColorSchemeResources(colorResIds);
            } else {
                horizontal.setColorSchemeResources(colorResIds);
            }
        }

        public void setRefreshing(boolean refreshing) {
            if (orientation == LayoutConfig.Orientation.Vertical) {
                vertical.setRefreshing(refreshing);
            } else {
                horizontal.setRefreshing(refreshing);
            }
        }
    }
}
