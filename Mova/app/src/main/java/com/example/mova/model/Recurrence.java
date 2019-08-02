package com.example.mova.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recurrence {

    public final Key key;

    protected Recurrence(Key key) {
        this.key = key;
    }

    private static final String pattern = "([A-z]{1,2})(\\d{2})?(\\d{2})?";

    public static List<Recurrence> parse(String recurrenceStr) {
        List<Recurrence> result = new ArrayList<>();
        String[] parts = (recurrenceStr == null) ? new String[0] : recurrenceStr.split(",");
        for (String part : parts) {
            if (part.equals("")) continue; // Skip all empty strings
            result.add(parseIndividual(part));
        }
        return result;
    }

    private static Recurrence parseIndividual(String part) {
        Pattern re = Pattern.compile(pattern);
        Matcher matcher = re.matcher(part);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid recurrence string: \"" + part + "\" had no match.");
        }

        Key key = Key.fromString(matcher.group(1));
        Integer num1 = parseNullInt(matcher.group(2));
        Integer num2 = parseNullInt(matcher.group(3));

        return makeRecurrence(key, num1, num2);
    }

    private static Recurrence makeRecurrence(Key key, Integer num1, Integer num2) {
        switch (key) {
            case Monday:
            case Tuesday:
            case Wednesday:
            case Thursday:
            case Friday:
            case Saturday:
            case Sunday:
                return makeWeekly(key);
            case Month:
                return makeMonthly(num1);
            case Year:
                return makeYearly(num1, num2);
            case Empty:
            default:
                return makeEmpty();
        }
    }

    public static Recurrence makeWeekly(Key dayOfWeek) {
        return new Recurrence(dayOfWeek);
    }

    public static Recurrence makeMonthly(Integer day) {
        checkDay(day);
        return new MonthlyRecurrence(day);
    }

    public static Recurrence makeYearly(Integer month, Integer day) {
        checkMonth(month);
        checkDay(day);
        return new YearlyRecurrence(month, day);
    }

    public static Recurrence makeEmpty() {
        return new Recurrence(Key.Empty);
    }

    private static void checkDay(Integer day) {
        if (day == null)          throw new IllegalArgumentException("Invalid day: must be non-null.");
        if (day <= 0 || day > 31) throw new IllegalArgumentException("Invalid day: must be between 1 and 31.");
    }

    private static void checkMonth(Integer month) {
        if (month == null)            throw new IllegalArgumentException("Invalid month: must be non-null.");
        if (month <= 0 || month > 12) throw new IllegalArgumentException("Invalid month: must be between 1 and 12.");
    }

    private static Integer parseNullInt(String toNum) {
        try {
            return (toNum == null) ? null : Integer.parseInt(toNum);
        } catch (Exception e) {
            Log.e("Recurrence", "Failed to parse possibly null integer \"" + toNum + "\".", e);
            return null;
        }
    }

    public static String toString(List<Recurrence> recurrences) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < recurrences.size(); i++) {
            builder.append(recurrences.get(i).toString());
            if (i < recurrences.size() - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public static String add(String existingRecurrence, Recurrence recurrence) {
        String result = "";
        if (existingRecurrence == null) existingRecurrence = "";

        result += existingRecurrence;
        if (!existingRecurrence.equals("")) result += ",";
        result += recurrence.toString();

        return result;
    }

    @Override
    public String toString() {
        if (this.getClass() == MonthlyRecurrence.class) {
            return ((MonthlyRecurrence) this).toString();
        } else if (this.getClass() == YearlyRecurrence.class) {
            return ((YearlyRecurrence) this).toString();
        } else {
            return key.toString();
        }
    }

    public enum Key {
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday,
        Sunday,
        Month,
        Year,
        Empty;

        public String toString() {
            switch (this) {
                case Monday: return "M";
                case Tuesday: return "T";
                case Wednesday: return "W";
                case Thursday: return "Th";
                case Friday: return "F";
                case Saturday: return "Sa";
                case Sunday: return "S";
                case Month: return "Mo";
                case Year: return "Y";

                case Empty:
                default:
                    return "E";
            }
        }

        public static Key fromString(String str) {
            switch (str) {
                case "M":
                    return Monday;
                case "T":
                    return Tuesday;
                case "W":
                    return Wednesday;
                case "Th":
                    return Thursday;
                case "F":
                    return Friday;
                case "Sa":
                    return Saturday;
                case "S":
                    return Sunday;
                case "Mo":
                    return Month;
                case "Y":
                    return Year;
                case "E":
                    return Empty;
                default:
                    throw new IllegalArgumentException("Invalid recurrence key \"" + str + "\".");
            }
        }
    }
}
