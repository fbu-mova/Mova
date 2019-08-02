package com.example.mova.model;

public class YearlyRecurrence extends Recurrence {

    public final int month, day;

    protected YearlyRecurrence(int month, int day) {
        super(Key.Year);
        this.month = month;
        this.day = day;
    }
}
