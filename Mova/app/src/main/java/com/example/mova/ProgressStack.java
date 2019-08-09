package com.example.mova;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.mova.utils.AsyncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressStack extends FrameLayout {

    // -- VIEWS -- //

    @BindView(R.id.flRoot)     protected FrameLayout flRoot;
    @BindView(R.id.cvMask)     protected CardView cvMask;
    @BindView(R.id.llSections) protected LinearLayout llSections;

    // -- MANAGING STATE -- //

    // Orientation is currently fixed, but naming is flexible for variable orientation
    protected int thickness, length, totalValue;
    protected int maxValue; // -1 if no maximum

    protected SparseIntArray sections;
    protected List<Integer> colors;
    protected SparseArray<AsyncUtils.ItemCallback<Integer>> clickListeners;
    protected SparseArray<FrameLayout> sectionViews;

    protected List<Integer> showSections;
    protected Integer selectedSection;
    protected boolean sectionIsSelected;
    protected Queue<Change> changeQueue;

    public ProgressStack(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ProgressStack(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressStack(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ButterKnife.bind(this);

        // Offset cardview to hide bottom corners
        int borderRadius = getResources().getDimensionPixelOffset(R.dimen.borderRadius);
        CardView.LayoutParams params = (CardView.LayoutParams) cvMask.getLayoutParams();
        params.bottomMargin = -1 * borderRadius;

        // Set all state values
        totalValue = 0;
        maxValue = -1;

        sections = new SparseIntArray();
        colors = new ArrayList<>();
        clickListeners = new SparseArray<>();
        sectionViews = new SparseArray<>();

        showSections = new ArrayList<>();
        selectedSection = null;
        sectionIsSelected = false;
        changeQueue = new LinkedList<>();

        // Extract xml attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GoalProgressBar, 0, 0);
        try {
            Resources res = getContext().getResources();
            thickness = typedArray.getDimensionPixelOffset(
                    R.styleable.ProgressStack_stackThickness,
                    res.getDimensionPixelOffset(R.dimen.progressStackThickness));
            length = typedArray.getDimensionPixelOffset(
                    R.styleable.ProgressStack_stackLength,
                    res.getDimensionPixelOffset(R.dimen.progressStackLength));
        } finally {
            typedArray.recycle();
        }
    }

    public void add(int color) {
        if (contains(color)) throw new IllegalArgumentException("Color " + color + " has already been used; colors must be unique.");
        sections.put(color, 0);
        colors.add(color);
        changeQueue.add(new SectionChange(color, ChangeType.Add));
    }

    public boolean contains(int color) {
        return colors.contains(color);
    }

    public int valueOf(int color) {
        return sections.get(color);
    }

    public void setValue(int color, int value) {
        if (!contains(color)) add(color);
        int prevValue = valueOf(color);
        totalValue -= prevValue;
        sections.put(color, value);
        totalValue += value;
        changeQueue.add(new SectionValueChange(color, prevValue, value, ChangeType.SetValue));
        invalidate();
    }

    public void show(int color) {
        if (!showSections.contains(color)) {
            showSections.add(color);
            changeQueue.add(new SectionChange(color, ChangeType.Show));
            invalidate();
        }
    }

    public void showOnly(int color) {
        hideAllSections();
        show(color);
    }

    public void hide(int color) {
        if (showSections.contains(color)) {
            showSections.remove(color);
            changeQueue.add(new SectionChange(color, ChangeType.Hide));
            invalidate();
        }
    }

    public void hideAllSections() {
        if (showSections.size() > 0) {
            for (int color : showSections) {
                showSections.remove(color);
                changeQueue.add(new SectionChange(color, ChangeType.Remove));
            }
            invalidate();
        }
    }

    public boolean isShown(int color) {
        return showSections.contains(color);
    }

    public void selectSection(int color) {
        selectedSection = color;
        sectionIsSelected = true;
        changeQueue.add(new SectionChange(color, ChangeType.Select));
        for (int deselected : showSections) {
            changeQueue.add(new SectionChange(deselected, ChangeType.Deselect));
        }
        invalidate();
    }

    public void deselect() {
        selectedSection = null;
        sectionIsSelected = false;
        for (int color : showSections) {
            changeQueue.add(new SectionChange(color, ChangeType.NeutralSelect));
        }
        invalidate();
    }

    public boolean isSelected(int color) {
        return selectedSection == color;
    }

    public int totalValue() {
        return totalValue;
    }

    public void setMaxValue(int value) {
        changeQueue.add(new ValueChange(maxValue, value, ChangeType.SetMaxValue));
        maxValue = value;
        invalidate();
    }

    public void setOnClick(int color, AsyncUtils.ItemCallback<Integer> listener) {
        clickListeners.put(color, listener);
    }

    protected enum ChangeType {
        Show,
        Hide,
        Select,
        Deselect,
        NeutralSelect,
        Add,
        Remove,
        SetValue,
        SetMaxValue
    }

    protected static class Change {
        final ChangeType type;

        Change(ChangeType type) {
            this.type = type;
        }
    }

    protected static class SectionChange extends Change {
        final int color;

        SectionChange(int color, ChangeType type) {
            super(type);
            this.color = color;
        }
    }

    protected static class ValueChange extends Change {
        final int prevValue;
        final int newValue;

        ValueChange(int prevValue, int newValue, ChangeType type) {
            super(type);
            this.prevValue = prevValue;
            this.newValue = newValue;
        }
    }

    protected static class SectionValueChange extends SectionChange {
        final int prevValue;
        final int newValue;

        SectionValueChange(int color, int prevValue, int newValue, ChangeType type) {
            super(color, type);
            this.prevValue = prevValue;
            this.newValue = newValue;
        }
    }

    // -- DRAWING -- //

    private static final float SELECTED_OPACITY = 1f;
    private static final float DESELECTED_OPACITY = 0.5f;
    private static final int DURATION = 200;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        while (changeQueue.size() > 0) {
            Change change = changeQueue.remove();

            if (change.type == ChangeType.SetMaxValue) {
                // TODO
            }

            int color = ((SectionChange) change).color;
            switch (change.type) {
                case Add:
                    addSection(color);
                case Show:
                    if (!isShown(color)) {
                        drawValueChange(color, valueOf(color), null);
                    }
                    break;

                case Hide:
                    if (isShown(color)) {
                        drawValueChange(color, 0, null);
                    }
                    break;

                case Remove:
                    if (isShown(color)) {
                        removeSection(color);
                    } else {
                        drawValueChange(color, 0, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                removeSection(color);
                            }
                        });
                    }
                    break;

                case Select:
                case NeutralSelect:
                    if (isShown(color)) {
                        drawSectionOpacity(color, SELECTED_OPACITY, null);
                    }
                    break;

                case Deselect:
                    if (isShown(color)) {
                        drawSectionOpacity(color, DESELECTED_OPACITY, null);
                    }
                    break;

                case SetValue:
                    if (showSections.contains(color)) {
                        int from = ((SectionValueChange) change).prevValue;
                        int to = ((SectionValueChange) change).newValue;
                        drawValueChange(color, to, null);
                    }
                    break;

                default: break;
            }
        }
    }

    protected void addSection(int color) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        FrameLayout view = new FrameLayout(getContext());
        view.setLayoutParams(params);
        view.setAlpha(1f);

        sectionViews.put(color, view);
        llSections.addView(view, 0);
    }

    protected void removeSection(int color) {
        FrameLayout view = sectionViews.get(color);
        sectionViews.remove(color);
        llSections.removeView(view);
    }

    protected void drawValueChange(int color, int value, @Nullable Animator.AnimatorListener listener) {
        final FrameLayout view = sectionViews.get(color);
        int fromHeight = view.getMeasuredHeight();
        int toHeight = toLength(value);

        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener((ValueAnimator animation) -> {
            int val = (Integer) animation.getAnimatedValue();
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.height = val;
            view.setLayoutParams(params);
        });

        animator.setDuration(DURATION)
                .addListener(listener);

        animator.start();
    }

    protected void drawSectionOpacity(int color, float opacity, @Nullable Animator.AnimatorListener listener) {
        FrameLayout view = sectionViews.get(color);
        view.animate()
            .alpha(opacity)
            .setDuration(DURATION)
            .setListener(listener);
    }

    protected int toLength(int value) {
        int max = Math.max(totalValue, maxValue);
        int length = flRoot.getMeasuredHeight();
        float scaleBy = (float) value / (float) max;
        float result = scaleBy * (float) length;
        return Math.round(result); // FIXME: Is this the correct size, or should I round some other way?
    }
}
