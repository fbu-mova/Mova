package com.example.mova.containers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoaderView extends FrameLayout {

    private boolean refreshing;

    @BindView(R.id.ivLoader) protected ImageView ivLoader;

    public LoaderView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public LoaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_loader, this);
        ButterKnife.bind(this, this);
        setRefreshing(false);
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        setVisibility((refreshing) ? View.VISIBLE : View.GONE);
        // TODO: Start/stop animation
    }
}
