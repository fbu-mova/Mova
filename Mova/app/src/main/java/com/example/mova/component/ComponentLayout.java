package com.example.mova.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mova.activities.DelegatedResultActivity;
import com.example.mova.utils.ViewUtils;

public class ComponentLayout extends FrameLayout {
    private int marginLeft = 0;
    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;

    private boolean autoClear = true;

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
        if (autoClear) clear(); // in case of RecyclerViews, discards old layout
        Component.Inflater inflater = component.makeInflater();
        Component.ViewHolder holder = inflater.inflate(activity, this, true);
//        ViewUtils.setMargins(holder.getView(), marginLeft, marginTop, marginRight, marginBottom);
        holder.getView().setClipToOutline(false);
        setPadding(marginLeft, marginTop, marginRight, marginBottom);
        component.render(activity, holder);
    }

    /**
     * Removes all components from the layout.
     * @source https://stackoverflow.com/questions/8020997/removing-all-child-views-from-view
     *         Has some interesting info on removing only certain kinds of views from a layout--helpful!
     */
    public void clear() {
        removeAllViews();
    }

    /**
     * Determines whether or not to automatically clear the inflater on inflating a new component.
     * @param autoClear Whether or not to automatically clear.
     */
    public void setAutoClear(boolean autoClear) {
        this.autoClear = autoClear;
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
