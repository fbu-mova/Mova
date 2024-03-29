package com.example.mova.containers;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mova.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EndlessScrollLayout<VH extends RecyclerView.ViewHolder> extends FrameLayout {

    protected RecyclerView.Adapter<VH> adapter;
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected LayoutConfig config;
    protected ScrollLoadHandler<VH> handler;

    @BindView(R.id.rvItems) protected RecyclerView rvItems;

    public EndlessScrollLayout(@NonNull Context context) {
        super(context);
    }

    public EndlessScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EndlessScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(ScrollLoadHandler<VH> handler) {
        init(new LayoutConfig(), handler);
    }

    public void init(LayoutConfig config, ScrollLoadHandler<VH> handler) {
        int layoutId = makeLayout(config);
        inflate(getContext(), layoutId, this);
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
                Log.d("ESL", "Hit load more");
                // Triggered only when new data needs to be appended to the list
                handler.loadMore();
            }
        };

        // Add the scroll listener to RecyclerView
        rvItems.addOnScrollListener(scrollListener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        rvItems.addItemDecoration(decoration);
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

        if      (xmp && ymp)  layoutId = R.layout.layout_esl_xmp_ymp;
        else if (xmp && !ymp) layoutId = R.layout.layout_esl_xmp_ywc;
        else if (!xmp && ymp) layoutId = R.layout.layout_esl_xwc_ymp;
        else /* xwc && ywc */ layoutId = R.layout.layout_esl_xwc_ywc;

        return layoutId;
    }

    public static class LayoutConfig {
        public Size widthSize;
        public Size heightSize;

        public LayoutConfig() {
            widthSize = Size.match_parent;
            heightSize = Size.match_parent;
        }

        public LayoutConfig setOrientation(Orientation orientation) {
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
