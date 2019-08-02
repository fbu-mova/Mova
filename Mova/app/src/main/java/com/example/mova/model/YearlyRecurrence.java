package com.example.mova.model;

import androidx.annotation.Nullable;

public class YearlyRecurrence extends Recurrence {

    public final int month, day;

    protected YearlyRecurrence(int month, int day) {
        super(Key.Year);
        this.month = month;
        this.day = day;
    }

    @Override
    public String toString() {
        return key.toString() + month + day;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != MonthlyRecurrence.class) return false;
        return key   == ((YearlyRecurrence) obj).key
            && day   == ((YearlyRecurrence) obj).day
            && month == ((YearlyRecurrence) obj).month;
    }
}
