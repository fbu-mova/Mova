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
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.mova.utils.AsyncUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressStack extends FrameLayout {

    // -- VIEWS -- //

    @BindView(R.id.flRoot)     protected FrameLayout flRoot;
    @BindView(R.id.cvMask)     protected CardView cvMask;
    @BindView(R.id.llSections) protected LinearLayout llSections;

    private static final float SELECTED_OPACITY = 1f;
    private static final float DESELECTED_OPACITY = 0.5f;

    private int valueAnimDuration;
    private int selectAnimDuration;

    // -- MANAGING STATE -- //

    // Orientation is currently fixed, but naming is flexible for variable orientation
    private int thickness, length;
    private int totalValue, totalValueShown;
    private int maxValue; // -1 if no maximum

    private SparseIntArray sections;
    private List<Integer> colors;
    private SparseArray<AsyncUtils.TwoItemCallback<Integer, FrameLayout>> clickListeners;
    private SparseArray<FrameLayout> sectionViews;

    private List<Integer> showSections;
    private Integer selectedSection;
    private boolean sectionIsSelected;
    private Queue<Change> changeQueue;

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
        inflate(getContext(), R.layout.layout_progress_stack, this);
        ButterKnife.bind(this);

        // Set all state values
        totalValue = 0;
        totalValueShown = 0;
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

            valueAnimDuration = typedArray.getInt(R.styleable.ProgressStack_valueAnimDuration, 700);
            selectAnimDuration = typedArray.getInt(R.styleable.ProgressStack_selectAnimDuration, 300);
        } finally {
            typedArray.recycle();
        }

        // Offset cardview to hide bottom corners
        int borderRadius = getResources().getDimensionPixelOffset(R.dimen.borderRadius);
        CardView.LayoutParams cvParams = (CardView.LayoutParams) cvMask.getLayoutParams();
        cvParams.bottomMargin = -1 * borderRadius;
        cvMask.setLayoutParams(cvParams);

        // Modify size based on parameters
        LayoutParams flParams = (LayoutParams) flRoot.getLayoutParams();
        flParams.width = thickness;
        flParams.height = length;
        flRoot.setLayoutParams(flParams);
    }

    public void add(int color) {
        if (contains(color)) throw new IllegalArgumentException("Color " + color + " has already been used; colors must be unique.");
        sections.put(color, 0);
        colors.add(color);
        changeQueue.add(new SectionChange(color, ChangeType.Add));
    }

    public void remove(int color) {
        if (!contains(color)) return;

        int value = valueOf(color);
        boolean wasShown = showSections.contains(color);

        sections.removeAt(color);
        colors.remove(colors.indexOf(color));
        clickListeners.remove(color);
        sectionViews.remove(color);
        showSections.remove(color);

        totalValue -= value;
        if (wasShown) totalValueShown -= value;

        changeQueue.add(new SectionChange(color, ChangeType.Remove));

        invalidate();
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
            totalValueShown += valueOf(color);
            changeQueue.add(new SectionChange(color, ChangeType.Show));
            invalidate();
        }
    }

    public void showOnly(int color) {
        hideAll();
        show(color);
    }

    public void hide(int color) {
        int index = showSections.indexOf(color);
        if (index >= 0) {
            showSections.remove(index);
            totalValueShown -= valueOf(color);
            changeQueue.add(new SectionChange(color, ChangeType.Hide));
            invalidate();
        }
    }

    public void hideAll() {
        if (showSections.size() > 0) {
            for (int color : showSections) {
                showSections.remove(showSections.indexOf(color));
                changeQueue.add(new SectionChange(color, ChangeType.Hide));
            }
            invalidate();
        }
    }

    public boolean isShown(int color) {
        return showSections.contains(color);
    }

    public void select(int color) {
        selectedSection = color;
        sectionIsSelected = true;
        changeQueue.add(new SectionChange(selectedSection, ChangeType.Select));
        for (int deselected : showSections) {
            if (deselected == selectedSection) continue;
            changeQueue.add(new SectionChange(deselected, ChangeType.Deselect));
        }
        invalidate();
    }

    public void selectNone() {
        selectedSection = null;
        sectionIsSelected = true;
        for (int color : showSections) {
            changeQueue.add(new SectionChange(color, ChangeType.Deselect));
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

    public int totalValueShown() {
        return totalValueShown;
    }

    public void setMaxValue(int value) {
        changeQueue.add(new ValueChange(maxValue, value, ChangeType.SetMaxValue));
        maxValue = value;
        invalidate();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setOnClick(int color, AsyncUtils.TwoItemCallback<Integer, FrameLayout> listener) {
        clickListeners.put(color, listener);

        FrameLayout view = sectionViews.get(color);
        if (view != null) view.setOnClickListener((v) -> listener.call(color, view));
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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        draw();
    }

    protected void draw() {
        while (changeQueue.size() > 0) {
            Change change = changeQueue.remove();

            if (change.type == ChangeType.SetMaxValue) {
                for (int color : showSections) {
                    drawValueChange(color, valueOf(color), null);
                }
                continue;
            }

            int color = ((SectionChange) change).color;
            switch (change.type) {
                case Add:
                    addSection(color);
                case Show:
                    drawValueChange(color, valueOf(color), null);
                    break;

                case Hide:
                    drawValueChange(color, 0, null);
                    break;

                case Remove:
                    drawValueChange(color, 0, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeSection(color);
                        }
                    });
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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        FrameLayout view = new FrameLayout(getContext());

        view.setLayoutParams(params);
        view.setAlpha(1f);
        view.setBackgroundColor(color);

        AsyncUtils.TwoItemCallback<Integer, FrameLayout> clickListener = clickListeners.get(color);
        if (clickListener != null) view.setOnClickListener((v) -> clickListener.call(color, view));

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
        if (view == null) return;

        int fromHeight = view.getHeight();
        int toHeight = toLength(value);

        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, toHeight);
        animator.addUpdateListener((ValueAnimator animation) -> {
            int val = (Integer) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = val;
            view.setLayoutParams(params);
        });

        animator.setDuration(valueAnimDuration);
        if (listener != null) animator.addListener(listener);

        animator.start();
    }

    protected void drawSectionOpacity(int color, float opacity, @Nullable Animator.AnimatorListener listener) {
        FrameLayout view = sectionViews.get(color);
        if (view == null) return;

        view.animate()
            .alpha(opacity)
            .setDuration(selectAnimDuration)
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
