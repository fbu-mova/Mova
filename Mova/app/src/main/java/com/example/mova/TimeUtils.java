package com.example.mova;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    // -- STRINGIFICATION -- //

    public static String toTimeString(Date date) {
        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm aa", Locale.US);
        return timeFmt.format(date);
    }

    public static String toDateString(Date date) {
        SimpleDateFormat dateFmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        return dateFmt.format(date);
    }

    // -- DATE NORMALIZATION, COMPARISON, AND KEYING -- //

    /**
     * Gets the current date regardless of time.
     * @return Today's date at midnight.
     */
    public static Date getToday() {
        return normalizeToDay(new Date());
    }

    /**
     * Converts a date to its representation for an entire day.
     * @param date A date to normalize.
     * @return The date at midnight.
     */
    public static Date normalizeToDay(Date date) {
        SimpleDateFormat dayFmt = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat fullFmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS", Locale.US);
        String day = dayFmt.format(date);
        try {
            Date dayOnly = fullFmt.parse(day + " 00:00:00:000");
            return dayOnly;
        } catch (ParseException exception) {
            // FIXME: Maybe bad to have a silent error like this?
            return date;
        }
    }
}
