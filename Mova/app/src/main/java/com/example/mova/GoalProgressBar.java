package com.example.mova;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class GoalProgressBar extends View {
    // code derived from: https://guides.codepath.org/android/Progress-Bar-Custom-View
        // skipped custom indicator type with enums
        // skipped saving instance state (across screen rotations)

    // Attributes

    private int progress;

    /** Bar color for the filled section (progress completed). */
    private int filledColor;
    /** Bar color for the unfilled section (remaining progress). */
    private int unfilledColor;

    /** Thickness of the progress bar. */
    private int thickness;
    /** The maximum length of the progress bar; if the parent container is larger, rounds the bottom. */
    private int maxLength;

    /** Orientation of the progress bar. (0: horizontal, 1: vertical) */
    private int orientation;
    /** Whether or not to round the first end of the progress bar. */
    private boolean roundStart;
    /** The side of the bar to round when length exceeds maxLength. (0: start, 1: end) */
    private int roundSide;
    /** The end of the bar from which to start drawing progress. (0: start, 1: end) */
    private int drawFrom;

    // Animation & values

    private ValueAnimator barAnimator;
    private Paint progressPaint;

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
            setMaxLength(typedArray.getInt(R.styleable.GoalProgressBar_maxLength, Integer.MAX_VALUE));

            setOrientation(typedArray.getInt(R.styleable.GoalProgressBar_barOrientation, 1));
            setRoundStart(typedArray.getBoolean(R.styleable.GoalProgressBar_roundStart, false));
            setRoundSide(typedArray.getInt(R.styleable.GoalProgressBar_barRoundSide, 1));
            setDrawFrom(typedArray.getInt(R.styleable.GoalProgressBar_drawFrom, (orientation == 0) ? 0 : 1));

            setProgress(typedArray.getInt(R.styleable.GoalProgressBar_progress, 0));
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
        int half = (orientation == 0) ? getHeight() / 2 : getWidth() / 2;
        int progressLength = (orientation == 0) ? (int) (getWidth() * progress / 100f) : (int) (getHeight() * progress / 100f);

        // draw the part of the bar that's filled (completed)
        progressPaint.setStrokeWidth(thickness);
        progressPaint.setColor(filledColor);

        if (orientation == 0) {
            canvas.drawLine(
                (drawFrom == 0) ? 0 : getWidth(), half,
                (drawFrom == 0) ? progressLength : getWidth() - progressLength, half,
                progressPaint
            );
        } else {
            canvas.drawLine(
                half, (drawFrom == 0) ? 0 : getHeight(),
                half, (drawFrom == 0) ? progressLength : getHeight() - progressLength,
                progressPaint
            );
        }

        // draw the unfilled section
        progressPaint.setColor(unfilledColor);

        if (orientation == 0) {
            canvas.drawLine(
                (drawFrom == 0) ? progressLength : getWidth() - progressLength, half,
                (drawFrom == 0) ? getWidth() : 0, half,
                progressPaint
            );
        } else {
            canvas.drawLine(
                half, (drawFrom == 0) ? progressLength : getHeight() - progressLength,
                half, (drawFrom == 0) ? getHeight() : 0,
                progressPaint
            );
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

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        invalidate();
    }

    public void setOrientation(int orientation) {
        if (!(orientation == 0 || orientation == 1)) {
            throw new IllegalArgumentException("Orientation must be 0 for horizontal or 1 for vertical; " + orientation + " is not a valid value.");
        }
        this.orientation = orientation;
        invalidate();
    }

    public void setRoundStart(boolean roundStart) {
        this.roundStart = roundStart;
        invalidate();
    }

    public void setRoundSide(int roundSide) {
        this.roundSide = roundSide;
        invalidate();
    }

    public void setDrawFrom(int drawFrom) {
        this.drawFrom = drawFrom;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        setProgress(progress, false);
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
