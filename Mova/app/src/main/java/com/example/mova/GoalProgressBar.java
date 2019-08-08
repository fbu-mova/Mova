package com.example.mova;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.util.Measure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class GoalProgressBar extends View {
    // code derived from: https://guides.codepath.org/android/Progress-Bar-Custom-View
        // skipped custom indicator type with enums
        // skipped saving instance state (across screen rotations)

    // Attributes

    /** Bar color for the filled section (progress completed). */
    private int filledColor;
    /** Bar color for the unfilled section (remaining progress). */
    private int unfilledColor;
    /** Thickness of the progress bar. */
    private int thickness;
    /** Orientation of the progress bar (horizontal = 0, vertical = 1). */
    private int orientation;
    /** The maximum length of the progress bar; if the parent container is larger, rounds the bottom. */
    private int maxLength;
    /** Whether or not to round the first end of the progress bar. */
    private boolean roundTop;

    // Animation & progress

    private Paint progressPaint;
    private ValueAnimator barAnimator;
    private int progress;

    public static final int PROGRESS_MAX = 100;

    public GoalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // extract custom attributes
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.GoalProgressBar, 0, 0);

        try {
            // extract attributes to member variables from typedArray
            setFilledColor(typedArray.getColor(R.styleable.GoalProgressBar_filledColor, getResources().getColor(R.color.blueMid)));
            setUnfilledColor(typedArray.getColor(R.styleable.GoalProgressBar_unfilledColor, getResources().getColor(R.color.blueUltraLight)));
            setThickness(typedArray.getDimensionPixelOffset(R.styleable.GoalProgressBar_thickness, getResources().getDimensionPixelSize(R.dimen.elementMargin)));
            setOrientation(typedArray.getInt(R.styleable.GoalProgressBar_orientation, 1));
            setMaxLength(typedArray.getInt(R.styleable.GoalProgressBar_maxLength, 0));
            setRoundTop(typedArray.getBoolean(R.styleable.GoalProgressBar_roundTop, false));
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Handles the sizing of our view, allowing for customization based on the goal indicator.
     * Good explanation of this method: https://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
     * @param widthMeasureSpec The 'instructions'/'constraints' of the width.
     * @param heightMeasureSpec The 'instructions'/'constraints' of the height.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get all relevant information from measure specs
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Set width and height based on measure spec
        int width, height;

        if (orientation == 0) {
            width = Math.min(parentWidth, maxLength);
            height = meetMeasureSpec(heightMode, parentHeight, thickness);
        } else {
            height = Math.min(parentHeight, maxLength);
            width = meetMeasureSpec(widthMode, parentWidth, thickness);
        }

        setMeasuredDimension(width, height);
    }

    protected int meetMeasureSpec(int measureSpecMode, int parent, int desired) {
        switch (measureSpecMode) {
            case MeasureSpec.EXACTLY:
                return parent;
            case MeasureSpec.AT_MOST:
                return Math.min(parent, desired);
            case MeasureSpec.UNSPECIFIED:
            default:
                return desired;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Set line dimensions
        int half = (orientation == 0) ? getHeight() / 2: getWidth() / 2;
        int progressEnd = (orientation == 0) ? (int) (getWidth() * progress / 100f) : (int) (getHeight() * progress / 100f);

        // draw the part of the bar that's filled (completed)
        progressPaint.setStrokeWidth(thickness);
        progressPaint.setColor(filledColor);

        if (orientation == 0) {
            canvas.drawLine(0, half, progressEnd, half, progressPaint);
        } else {
            canvas.drawLine(half, 0, half, progressEnd, progressPaint);
        }

        // draw the unfilled section
        progressPaint.setColor(unfilledColor);

        if (orientation == 0) {
            canvas.drawLine(progressEnd, half, getWidth(), half, progressPaint);
        } else {
            canvas.drawLine(half, progressEnd, half, getHeight(), progressPaint);
        }

        // TODO: Add masking on should round
    }

    public void setFilledColor(int filledColor) {
        this.filledColor = filledColor;
        invalidate();
    }

    public void setUnfilledColor(int unfilledColor) {
        this.unfilledColor = unfilledColor;
        invalidate();
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        invalidate();
    }

    public void setOrientation(int orientation) {
        if (!(orientation == 0 || orientation == 1)) {
            throw new IllegalArgumentException("Orientation must be 0 for horizontal or 1 for vertical; " + orientation + " is not a valid value.");
        }
        this.orientation = orientation;
        invalidate();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        invalidate();
    }

    public void setRoundTop(boolean roundTop) {
        this.roundTop = roundTop;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setProgress(progress, true);
    }

    private void setProgress(int progress, boolean animate) {
        if (animate) {
            barAnimator = ValueAnimator.ofFloat(0, 1);

            barAnimator.setDuration(700);

            // reset progress without animating
            setProgress(0, false);

            barAnimator.setInterpolator(new DecelerateInterpolator());

            barAnimator.addUpdateListener((ValueAnimator animation) -> {
                float interpolation = (float) animation.getAnimatedValue();
                setProgress((int) (interpolation * progress), false);
            });

            if (!barAnimator.isStarted()) {
                barAnimator.start();
            }
        } else {
            this.progress = progress;
            postInvalidate();
        }
    }
}
