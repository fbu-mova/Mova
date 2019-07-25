package com.example.mova;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class GoalProgressBar extends View {
    // code derived from: https://guides.codepath.org/android/Progress-Bar-Custom-View
        // skipped custom indicator type with enums
        // skipped saving instance state (across screen rotations)

    // for the view customizations

    // height of the goal indicator -- used to indicate goal completion, may not use (aka goal does not have to be 100% done to be complete)
    private float goalIndicatorHeight;
    // thickness of the goal indicator
    private float goalIndicatorWidth;
    // bar color when the goal has been reached
    private int goalReachedColor;
    // bar color when the goal has not been reached
    private int goalNotReachedColor;
    // bar color for the unfilled section (remaining progress)
    private int unfilledSectionColor;
    // height of the progress bar
    private float barHeight;

    // for coloring/drawing/filling
    private Paint progressPaint;

    // animation to fill progress bar (too much if done for all progress bars?)
    private ValueAnimator barAnimator;

    // data of a goal
    private int progress;
    private int goal;
    private boolean isGoalReached;

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
            setGoalIndicatorHeight(typedArray.getDimension(R.styleable.GoalProgressBar_goalIndicatorHeight, 10));
            setGoalIndicatorWidth(typedArray.getDimensionPixelSize(R.styleable.GoalProgressBar_goalIndicatorWidth, 5));

            setGoalReachedColor(typedArray.getColor(R.styleable.GoalProgressBar_goalReachedColor, Color.BLUE));
            setGoalNotReachedColor(typedArray.getColor(R.styleable.GoalProgressBar_goalNotReachedColor, Color.BLACK));
            setUnfilledSectionColor(typedArray.getColor(R.styleable.GoalProgressBar_unfilledSectionColor, Color.RED));
            setBarHeight(typedArray.getDimensionPixelOffset(R.styleable.GoalProgressBar_barHeight, 4));

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
        // set width to be same width as parent
        int width = MeasureSpec.getSize(widthMeasureSpec);

        // set height -- defined by the goal indicator, which we won't be using
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                // we must be exactly the given size
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                // can't be bigger than specified height
                height = (int) Math.min(goalIndicatorHeight, heightSize);
                break;
            default:
                // we can be whatever height we want
                height = (int) goalIndicatorHeight;
                break;
        }

        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int halfHeight = getHeight() / 2;
        int progressEndX = (int) (getWidth() * progress / 100f);

        // draw the part of the bar that's filled (completed)
        progressPaint.setStrokeWidth(barHeight);
        progressPaint.setColor(isGoalReached ? goalReachedColor : goalNotReachedColor);
        canvas.drawLine(0, halfHeight, progressEndX, halfHeight, progressPaint);

        // draw the unfilled section
        progressPaint.setColor(unfilledSectionColor);
        canvas.drawLine(progressEndX, halfHeight, getWidth(), halfHeight, progressPaint);

        // draw the goal indicator
        float indicatorPosition = getWidth() * goal / 100f;
        progressPaint.setColor(goalReachedColor);
        progressPaint.setStrokeWidth(goalIndicatorWidth);
        canvas.drawLine(indicatorPosition, halfHeight - goalIndicatorHeight / 2,
                indicatorPosition, halfHeight + goalIndicatorHeight / 2, progressPaint);
    }

    public void setGoalIndicatorHeight(float goalIndicatorHeight) {
        this.goalIndicatorHeight = goalIndicatorHeight;
        invalidate();
    }

    public void setGoalIndicatorWidth(float goalIndicatorWidth) {
        this.goalIndicatorWidth = goalIndicatorWidth;
        invalidate();
    }

    public void setGoalReachedColor(int goalReachedColor) {
        this.goalReachedColor = goalReachedColor;
        invalidate();
    }

    public void setGoalNotReachedColor(int goalNotReachedColor) {
        this.goalNotReachedColor = goalNotReachedColor;
        invalidate();
    }

    public void setUnfilledSectionColor(int unfilledSectionColor) {
        this.unfilledSectionColor = unfilledSectionColor;
        invalidate();
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        updateGoalReached();
        invalidate();

//        setProgress(progress, true);
    }

    private void setProgress(int progress, boolean animate) {
        if (animate) {
            barAnimator = ValueAnimator.ofFloat(0,1);
            barAnimator.setDuration(700);

            // reset progress without animating
            setProgress(0, false);

            barAnimator.setInterpolator(new DecelerateInterpolator());

            barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float interpolation = (float) animation.getAnimatedValue();
                    setProgress((int) (interpolation * progress), false);
                }
            });

            if(!barAnimator.isStarted()) {
                barAnimator.start();
            }
        else {
            this.progress = progress;
            invalidate();
        }
        }
    }

    public void setGoal(int goal) {
        this.goal = goal;
        updateGoalReached();
        invalidate();
    }

    private void updateGoalReached() {
        isGoalReached = progress >= goal;
    }

}
