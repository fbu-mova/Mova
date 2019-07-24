package com.example.mova.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.R;
import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.utils.ViewUtils;

public class ComponentLayout extends FrameLayout {
    private int margin = 16;

    public ComponentLayout(@NonNull Context context) {
        super(context);
    }

    public ComponentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ComponentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * Inflates a component into the layout.
     * @param activity The activity to which the layout belongs.
     * @param component The component to inflate.
     */
    public void inflateComponent(DelegatedResultActivity activity, Component component) {
        ViewGroup parent = this;
        component.makeViewHolder(activity, parent, true);
        ViewUtils.setMargins(component.getView(), margin * 2);
        component.render();
    }

    /**
     * Removes all components from the layout.
     * @source https://stackoverflow.com/questions/8020997/removing-all-child-views-from-view
     *         Has some interesting info on removing only certain kinds of views from a layout--helpful!
     */
    public void clear() {
        removeAllViews();
    }
}
