package dev.grcq.nitrolib.core.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@UtilityClass
public class TimeUtil {

    private static final DateFormat HHMMSS_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String secondsToFormattedTime(int seconds) {
        return secondsToFormattedTime(seconds, " ", true, true, true, false);
    }

    public static String secondsToFormattedTime(int seconds, String separator) {
        return secondsToFormattedTime(seconds, separator, true, true, true, false);
    }

    public static String secondsToFormattedTime(int seconds, String separator, boolean shortUnits) {
        return secondsToFormattedTime(seconds, separator, true, true, true, shortUnits);
    }

    public static String secondsToFormattedTime(int seconds, String separator, boolean removeLeadingZeros, boolean removeTrailingZeros) {
        return secondsToFormattedTime(seconds, separator, removeLeadingZeros, removeTrailingZeros, false, false);
    }

    public static String secondsToFormattedTime(int seconds, String separator, boolean removeLeadingZeros, boolean removeTrailingZeros, boolean removeEmptyUnits) {
        return secondsToFormattedTime(seconds, separator, removeLeadingZeros, removeTrailingZeros, removeEmptyUnits, false);
    }

    public static String secondsToFormattedTime(int seconds, boolean shortUnits) {
        return secondsToFormattedTime(seconds, " ", true, true, true, shortUnits);
    }

    public static String secondsToFormattedTime(int seconds, boolean removeLeadingZeros, boolean removeTrailingZeros) {
        return secondsToFormattedTime(seconds, " ", removeLeadingZeros, removeTrailingZeros, false, false);
    }

    public static String secondsToFormattedTime(int seconds, boolean removeLeadingZeros, boolean removeTrailingZeros, boolean removeEmptyUnits) {
        return secondsToFormattedTime(seconds, " ", removeLeadingZeros, removeTrailingZeros, removeEmptyUnits, false);
    }

    public static String secondsToFormattedTime(int seconds, boolean removeLeadingZeros, boolean removeTrailingZeros, boolean removeEmptyUnits, boolean shortUnits) {
        return secondsToFormattedTime(seconds, " ", removeLeadingZeros, removeTrailingZeros, removeEmptyUnits, shortUnits);
    }

    public static String secondsToFormattedTime(int seconds, String separator, boolean removeLeadingZeros, boolean removeTrailingZeros, boolean removeEmptyUnits, boolean shortUnits) {
        if (seconds <= 0) return "0" + (shortUnits ? "s" : " seconds");

        int remaining = seconds;
        int years = remaining / 31536000;
        remaining = remaining % 31536000;
        int months = remaining / 2592000;
        remaining = remaining % 2592000;
        int weeks = remaining / 604800;
        remaining = remaining % 604800;
        int days = remaining / 86400;
        remaining = remaining % 86400;
        int hours = remaining / 3600;
        remaining = remaining % 3600;
        int minutes = remaining / 60;
        remaining = remaining % 60;

        int[] values = new int[] {years, months, weeks, days, hours, minutes, remaining};

        String[] unitsArray = new String[] {"year", "month", "week", "day", "hour", "minute", "second"};
        String[] shortUnitsArray = new String[] {"y", "mo", "w", "d", "h", "m", "s"};

        List<String> builders = Lists.newArrayList();
        for (int i = 0; i < values.length; i++) {
            String str = values[i] +
                    (shortUnits ? shortUnitsArray[i] : " " + unitsArray[i]) +
                    (values[i] == 1 || shortUnits ? "" : "s");
            builders.add(str);
        }

        if (removeEmptyUnits) {
            for (int i = 0; i < builders.size(); i++) {
                String current = builders.get(i);
                if (current.charAt(0) == '0') {
                    builders.remove(i);
                    i--;
                }
            }
        } else {
            boolean lastIsZero = true;
            for (int i = 0; i < builders.size(); i++) {
                if (!lastIsZero) break;

                String current = builders.get(i);
                if (current.charAt(0) == '0' && removeLeadingZeros) {
                    builders.remove(i);
                    i--;
                    continue;
                }

                lastIsZero = false;
            }

            lastIsZero = true;
            for (int i = builders.size() - 1; i >= 0; i--) {
                if (!lastIsZero) break;

                String current = builders.get(i);
                if (current.charAt(0) == '0' && removeTrailingZeros) {
                    builders.remove(i);
                    i++;
                    continue;
                }

                lastIsZero = false;
            }
        }

        return String.join(separator, builders);
    }

    public static int formattedTimeToSeconds(@NotNull String formattedTime) {
        if (formattedTime.equals("0") || formattedTime.equalsIgnoreCase("0s")) return 0;

        int[] unitValues = new int[] {31536000, 2592000, 604800, 86400, 3600, 60, 1};
        String[] units = new String[] {"y", "mo", "w", "d", "h", "m", "s"};
        String[] split = formattedTime.replaceAll("\\s", "").split("");

        int seconds = 0;
        int current = 0;
        for (String s : split) {
            if (s.equals(" ")) continue;

            if (Character.isDigit(s.charAt(0))) {
                current = current * 10 + Integer.parseInt(s);
            } else {
                int index = Arrays.asList(units).indexOf(s);
                seconds += current * unitValues[index];
                current = 0;
            }
        }

        return seconds;
    }

    public static long formattedTimeToMillis(@NotNull String formattedTime) {
        return formattedTimeToSeconds(formattedTime) * 1000L;
    }

    public static String millisToHHMMSS(long millis) {
        return HHMMSS_FORMAT.format(new Date(millis));
    }

    public static String millisToDate(long millis) {
        return DATE_FORMAT.format(new Date(millis));
    }

    public static String millisToDateTime(long millis) {
        return DATE_TIME_FORMAT.format(new Date(millis));
    }

    public static String secondsToHHMMSS(int seconds) {
        return millisToHHMMSS(seconds * 1000L);
    }

    public static String secondsToDate(int seconds) {
        return millisToDate(seconds * 1000L);
    }

    public static String secondsToDateTime(int seconds) {
        return millisToDateTime(seconds * 1000L);
    }


}
