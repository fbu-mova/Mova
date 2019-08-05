package com.example.mova.model;

import java.util.Calendar;
import java.util.Date;

public class WeeklyRecurrence extends Recurrence {

    protected WeeklyRecurrence(Key key) {
        super(key);
    }

    @Override
    public Date nextRelativeDate(Date prevActionDate) {
        /** @source https://stackoverflow.com/questions/3463756/is-there-a-good-way-to-get-the-date-of-the-coming-wednesday */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prevActionDate);

        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int diff = toCalendarDayOfWeek(key) - weekday;
        if (diff <= 0) diff += 7;

        calendar.add(Calendar.DAY_OF_MONTH, diff);
        return calendar.getTime();
    }

    protected static int toCalendarDayOfWeek(Key key) {
        switch (key) {
            case Monday:    return Calendar.MONDAY;
            case Tuesday:   return Calendar.TUESDAY;
            case Wednesday: return Calendar.WEDNESDAY;
            case Thursday:  return Calendar.THURSDAY;
            case Friday:    return Calendar.FRIDAY;
            case Saturday:  return Calendar.SATURDAY;
            case Sunday:    return Calendar.SUNDAY;
            default:        throw new IllegalArgumentException("Key cannot be converted to calendar date; it does not represent a day of the week.");
        }
    }
}
