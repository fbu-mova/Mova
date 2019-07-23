package com.example.mova.utils;

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
        return setTime(date, "00:00:00:000");
    }

    /**
     * Parses string to a date using MM/dd/yyyy HH:mm:ss:SSS.
     * Example: 07/22/2019 00:05:59:000
     * @param str The string to parse.
     * @return The date produced.
     */
    public static Date fromString(String str) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
        return fmt.parse(str);
    }

    /**
     * Sets the time of a date to a given time.
     * Uses timeStr format: HH:mm:ss:SSS (HH is military time)
     * @param date The date to set to the desired time.
     * @param timeStr The time string to parse.
     * @return The resulting date.
     */
    public static Date setTime(Date date, String timeStr) {
        SimpleDateFormat dayFmt = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        SimpleDateFormat fullFmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS", Locale.US);
        String day = dayFmt.format(date);
        try {
            Date shifted = fullFmt.parse(day + " " + timeStr);
            return shifted;
        } catch (ParseException exception) {
            // FIXME: Maybe bad to have a silent error like this?
            return date;
        }
    }
}
