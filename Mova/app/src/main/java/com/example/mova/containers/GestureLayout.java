package com.example.mova.containers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GestureLayout extends FrameLayout {
    private List<GestureDetector> detectors = new ArrayList<>();

    public GestureLayout(@NonNull Context context) {
        super(context);
    }

    public GestureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (detectors.size() == 0) return super.onTouchEvent(event);

        boolean result = true;
        for (GestureDetector detector : detectors) {
            if (detector != null) result = result && detector.onTouchEvent(event);
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return false;
    }

    public void addGestureDetector(GestureDetector gestureDetector) {
        detectors.add(gestureDetector);
    }

    public void removeGestureDetector(GestureDetector gestureDetector) {
        detectors.remove(gestureDetector);
    }
}
