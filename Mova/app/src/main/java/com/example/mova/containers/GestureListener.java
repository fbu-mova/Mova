package com.example.mova.containers;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private final View view;
    private final int minMove;

    public GestureListener(View view) {
        this(view, 0);
    }

    /**
     * @param view
     * @param minMove The minimum amount of movement that must occur for a fling to be considered a swipe. Acts as a measure of inverted sensitivity.
     */
    public GestureListener(View view, int minMove) {
        this.view = view;
        this.minMove = minMove;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        view.onTouchEvent(e);
        return super.onSingleTapConfirmed(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onTouch();
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        List<Direction> directions = new ArrayList<>();

        if (e1.getX() < e2.getX())      directions.add(Direction.Left);
        else if (e1.getX() > e2.getX()) directions.add(Direction.Right);

        if (e1.getY() < e2.getY())      directions.add(Direction.Up);
        else if (e1.getY() > e2.getY()) directions.add(Direction.Down);

        if (directions.size() > 0) return onSwipe(directions);
        return onTouch();
    }

    public abstract boolean onTouch();
    public abstract boolean onSwipe(List<Direction> directions);

    public enum Direction {
        Left, Up, Right, Down
    }
}
