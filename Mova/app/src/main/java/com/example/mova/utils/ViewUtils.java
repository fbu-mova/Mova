package com.example.mova.utils;

import android.view.View;
import android.view.ViewGroup;

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
}
