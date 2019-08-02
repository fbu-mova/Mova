package com.example.mova.model;

import androidx.annotation.Nullable;

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
}
