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
        return toDateString(date, true);
    }

    public static String toDateString(Date date, boolean includeYear) {
        String fmtStr = "MMMM dd";
        if (includeYear) fmtStr += ", yyyy";
        SimpleDateFormat dateFmt = new SimpleDateFormat(fmtStr, Locale.US);
        return dateFmt.format(date);
    }

    public static String toShortDateString(Date date) {
        String fmtStr = "MMM dd";
        SimpleDateFormat dateFmt = new SimpleDateFormat(fmtStr, Locale.US);
        return dateFmt.format(date);
    }

    public static String toShortWeekdayString(Date date) {
        String fmtStr = "E";
        SimpleDateFormat dateFmt = new SimpleDateFormat(fmtStr, Locale.US);
        return Character.toString(dateFmt.format(date).charAt(0));
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

    public static int getYear(Date date) {
        SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy", Locale.US);
        return Integer.parseInt(yearFmt.format(date));
    }

    public static String toLongRelativeDateString(Date date) {
        Date now = new Date();

        // FIXME: Possible special case - dateMS >= normalizeToDay(nowMS)
        long dateMillis = date.getTime();
        long nowMillis = normalizeToDay(now).getTime();
        long weekAgoMillis = nowMillis - getManyDaysInMillis(7);

        if (dateMillis >= weekAgoMillis) {
            SimpleDateFormat dayOfWeekFmt = new SimpleDateFormat("EEEE", Locale.US);
            return dayOfWeekFmt.format(date);
        }

        int year = getYear(date);
        int nowYear = getYear(now);
        return toDateString(date, year == nowYear);
    }

    public static long getSecondInMillis() {
        return 1000;
    }

    public static long getMinuteInMillis() {
        return getSecondInMillis() * 60;
    }

    public static long getHourInMillis() {
        return getMinuteInMillis() * 60;
    }

    public static long getDayInMillis() {
        return getHourInMillis() * 24;
    }

    public static long getManyDaysInMillis(int numDays) {
        return getDayInMillis() * numDays;
    }

    public static Date getDaysAgo(Date fromDate, int numDaysAgo) {
        long days = TimeUtils.getManyDaysInMillis(numDaysAgo);
        return new Date(fromDate.getTime() - days);
    }
}
