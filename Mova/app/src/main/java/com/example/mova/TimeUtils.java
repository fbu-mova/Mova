package com.example.mova;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String getTime(Date date) {
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm aa", Locale.US);
        return timeFmt.format(date);
    }
}
