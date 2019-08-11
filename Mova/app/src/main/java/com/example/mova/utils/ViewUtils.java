package com.example.mova.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mova.R;

public class ViewUtils {
    /** @source https://stackoverflow.com/questions/4472429/change-the-right-margin-of-a-view-programmatically */
    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    public static void setMargins(View v, int margin) {
        setMargins(v, margin, margin, margin, margin);
    }

    public static void expandTouchArea(View view, Rect increaseBy) {
        final View parent = (View) view.getParent();  // button: the view you want to enlarge hit area
        parent.post(() -> {
            final Rect rect = new Rect();
            view.getHitRect(rect);
            rect.top -= increaseBy.top;
            rect.left -= increaseBy.left;
            rect.bottom += increaseBy.bottom;
            rect.right += increaseBy.right;
            parent.setTouchDelegate(new TouchDelegate(rect, view));
        });
    }
}
