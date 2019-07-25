package com.example.mova;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GoalProgressBar extends View {
    // code derived from: https://guides.codepath.org/android/Progress-Bar-Custom-View

    // for the view customizations

    // height of the goal indicator
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

    // data of a goal

    private int progress;
    private int goal;
    private boolean isGoalReached;

    public GoalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGoalIndicatorHeight(float goalIndicatorHeight) {
        this.goalIndicatorHeight = goalIndicatorHeight;
    }

    public void setGoalIndicatorWidth(float goalIndicatorWidth) {
        this.goalIndicatorWidth = goalIndicatorWidth;
    }

    public void setGoalReachedColor(int goalReachedColor) {
        this.goalReachedColor = goalReachedColor;
    }

    public void setGoalNotReachedColor(int goalNotReachedColor) {
        this.goalNotReachedColor = goalNotReachedColor;
    }

    public void setUnfilledSectionColor(int unfilledSectionColor) {
        this.unfilledSectionColor = unfilledSectionColor;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        updateGoalReached();
        invalidate();
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
