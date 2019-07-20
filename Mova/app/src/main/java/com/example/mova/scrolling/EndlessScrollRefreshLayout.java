package com.example.mova.scrolling;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mova.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class EndlessScrollRefreshLayout<Item, VH extends RecyclerView.ViewHolder> extends FrameLayout {

    protected RecyclerView.Adapter<VH> adapter;
    protected List<Item> items;
    protected EndlessRecyclerViewScrollListener scrollListener;

    @BindView(R.id.rvItems)         protected RecyclerView rvItems;
    @BindView(R.id.swipeContainer)  protected SwipeRefreshLayout swipeContainer;

    public EndlessScrollRefreshLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public EndlessScrollRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EndlessScrollRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_endless_scroll_refresh, this);
        ButterKnife.bind(this, this);

        // Configure RecyclerView
        items = new ArrayList<>();
        adapter = makeAdapter();

        rvItems.setAdapter(adapter);
        rvItems.addItemDecoration(new EdgeDecorator(getTopEdgeMargin(), getBottomEdgeMargin(), getLeftEdgeMargin(), getRightEdgeMargin()));

        RecyclerView.LayoutManager layoutManager = makeLayoutManager();
        rvItems.setLayoutManager(layoutManager);
        // Configure infinite scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadMore();
            }
        };

        // Add the scroll listener to RecyclerView
        rvItems.addOnScrollListener(scrollListener);

        // Add swipe to refresh actions
        swipeContainer.setOnRefreshListener(() -> {
            items.clear();
            adapter.notifyDataSetChanged();
            load();
        });
        swipeContainer.setColorSchemeResources(getColorScheme());
    }

    public abstract void load();
    public abstract void loadMore();

    public abstract RecyclerView.Adapter<VH> makeAdapter();
    public abstract RecyclerView.LayoutManager makeLayoutManager();

    public abstract int[] getColorScheme();

    public abstract int getTopEdgeMargin();
    public abstract int getBottomEdgeMargin();
    public abstract int getLeftEdgeMargin();
    public abstract int getRightEdgeMargin();
}
