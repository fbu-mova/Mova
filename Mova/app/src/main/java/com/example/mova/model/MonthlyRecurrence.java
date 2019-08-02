package com.example.mova.model;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class MonthlyRecurrence extends Recurrence {

    public final int day;

    protected MonthlyRecurrence(int day) {
        super(Key.Month);
        this.day = day;
    }

    @Override
    public String toString() {
        return key.toString() + day;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != MonthlyRecurrence.class) return false;
        return key == ((MonthlyRecurrence) obj).key
            && day == ((MonthlyRecurrence) obj).day;
    }

    @Override
    public Date nextRelativeDate(Date prevActionDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prevActionDate);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        // FIXME: This is not sensitive to the number of days in a month
        if (dayOfMonth > day) {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
}
