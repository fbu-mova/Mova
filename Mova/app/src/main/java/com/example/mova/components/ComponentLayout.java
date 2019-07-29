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
    private int marginLeft = 0;
    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;

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
    public void inflateComponent(DelegatedResultActivity activity, Component component) {
        component.makeViewHolder(activity, this, true);
        ViewUtils.setMargins(component.getView(), marginLeft, marginTop, marginRight, marginBottom);
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

    // -- MARGINS -- //

    public ComponentLayout setMargin(int margin) {
        marginLeft = margin;
        marginTop = margin;
        marginRight = margin;
        marginBottom = margin;
        return this;
    }

    public ComponentLayout setMargin(int left, int top, int right, int bottom) {
        marginLeft = left;
        marginTop = top;
        marginRight = right;
        marginBottom = bottom;
        return this;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public ComponentLayout setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public ComponentLayout setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public ComponentLayout setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public ComponentLayout setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }
}
