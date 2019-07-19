package com.example.mova.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ComponentLayout extends FrameLayout {
    public ComponentLayout(@NonNull Context context) {
        super(context);
    }

    public ComponentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ComponentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Inflates a component into the layout.
     * @param activity The activity to which the layout belongs.
     * @param component The component to inflate.
     */
    public void inflateComponent(Activity activity, Component component) {
        ViewGroup parent = findViewById(android.R.id.content); // FIXME: Make sure this works!
        component.makeViewHolder(activity, parent);
        component.bind();
    }
}
