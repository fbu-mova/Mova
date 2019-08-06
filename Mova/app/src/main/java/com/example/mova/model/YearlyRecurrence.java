package com.example.mova.model;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class YearlyRecurrence extends Recurrence {

    public final int month, day;

    protected YearlyRecurrence(int month, int day) {
        super(Key.Year);
        this.month = month;
        this.day = day;
    }

    public String toExpression() {
        return key.toExpression() + padInt(month) + padInt(day);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != MonthlyRecurrence.class) return false;
        return key   == ((YearlyRecurrence) obj).key
            && day   == ((YearlyRecurrence) obj).day
            && month == ((YearlyRecurrence) obj).month;
    }

    @Override
    public Date nextRelativeDate(Date prevActionDate) {
        Calendar prevCal = Calendar.getInstance();
        prevCal.setTime(prevActionDate);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.YEAR, prevCal.get(Calendar.YEAR));

        if (prevActionDate.compareTo(calendar.getTime()) >= 0) {
            calendar.add(Calendar.YEAR, 1);
        }

        return calendar.getTime();
    }

    /**
     * Determines whether a recurrence is strictly a yearly recurrence.
     * @param recurrence The recurrence to check.
     * @return Whether the recurrence is of this type.
     */
    public static boolean is(Recurrence recurrence) {
        return recurrence.key == Key.Year;
    }
}
