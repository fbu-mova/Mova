package com.example.mova.model;

public class MonthlyRecurrence extends Recurrence {

    public final int day;

    protected MonthlyRecurrence(int day) {
        super(Key.Month);
        this.day = day;
    }
}
