package com.example.mova.containers;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.mova.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: Refactor -> split into loader overlay and empty text in ESL/ESRL

public class EmptyLoaderRecyclerLayout extends FrameLayout {

    public static final String DEFAULT_EMPTY_TEXT = "Empty"; // TODO: Come up with better universal empty text

    private ListContainer items;

    private RecyclerView.Adapter adapter;

    @BindView(R.id.rvItems)  protected RecyclerView rvItems;
    @BindView(R.id.lvLoader) protected LoaderView lvLoader;
    @BindView(R.id.tvEmpty)  protected TextView tvEmpty;

    public EmptyLoaderRecyclerLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public EmptyLoaderRecyclerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EmptyLoaderRecyclerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_empty_loader_recycler, this);
        ButterKnife.bind(this, this);

        // Set empty text from attribute
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmptyLoaderRecyclerLayout);
        String str = typedArray.getString(R.styleable.EmptyLoaderRecyclerLayout_emptyText);
        if (str == null || str.equals("")) str = DEFAULT_EMPTY_TEXT;
        tvEmpty.setText(str);

        hideEmpty();
        lvLoader.setRefreshing(false);
    }

    public void setupRecyclerView(List items, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        this.items = new ListContainer(items);
        setupRecyclerView(adapter, layoutManager);
    }

    public void setupRecyclerView(SortedList items, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        this.items = new ListContainer(items);
        setupRecyclerView(adapter, layoutManager);
    }

    private void setupRecyclerView(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        this.adapter = adapter;

        rvItems.setLayoutManager(layoutManager);
        rvItems.setAdapter(adapter);

        updateEmpty();
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        rvItems.addItemDecoration(decor);
    }

    private void hideEmpty() {
        tvEmpty.setVisibility(GONE);
    }

    private void showEmpty() {
        tvEmpty.setVisibility(VISIBLE);
    }

    public void setRefreshing(boolean refreshing) {
        lvLoader.setRefreshing(refreshing);
    }

    public boolean isRefreshing() {
        return lvLoader.isRefreshing();
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    private void updateEmpty() {
        if (isRefreshing() || items.size() > 0) {
            hideEmpty();
        } else {
            showEmpty();
        }
    }

    protected static class ListContainer {
        List list;
        SortedList sList;

        ListContainer(List list) {
            this.list = list;
        }

        ListContainer(SortedList list) {
            this.sList = list;
        }

        int size() {
            return (sList == null) ? list.size() : sList.size();
        }
    }
}
