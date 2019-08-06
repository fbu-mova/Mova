package com.example.mova.utils;

import android.widget.TextView;

import com.google.android.gms.common.util.Strings;

import java.util.List;

public class TextUtils {
    public static <T> void writeCommaSeparated(List<T> items, String empty, TextView tv, AsyncUtils.ItemReturnCallback<T, String> toString) {
        if (items.size() == 0) {
            tv.setText(empty);
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            builder.append(toString.call(items.get(i)));
            if (i < items.size() - 1) builder.append(", ");
        }
        tv.setText(builder.toString());
    }

    public static String ellipsize(String str, int maxLength) {
        if (str.length() < maxLength) return str;
        String ellipses = "...";
        str = str.substring(maxLength - ellipses.length());
        str += ellipses;
        return str;
    }
}
