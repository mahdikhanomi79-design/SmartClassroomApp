package com.ruet.cse.smartclassroom.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {
    public static String getTodayDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        String[] days = {"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        return days[cal.get(Calendar.DAY_OF_WEEK) - 1];
    }
    public static String nowAsHHmm() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String to12Hour(String hhmm) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat out = new SimpleDateFormat("h:mm a", Locale.US);
            return out.format(in.parse(hhmm));
        } catch (Exception e) {
            return hhmm;
        }
    }

    public static boolean isBetween(String time, String start, String end) {
        return time.compareTo(start) >= 0 && time.compareTo(end) < 0;
    }

    public static int minutesBetween(String start, String end) {
        int[] s = split(start), e = split(end);
        return (e[0] * 60 + e[1]) - (s[0] * 60 + s[1]);
    }

    private static int[] split(String hhmm) {
        String[] parts = hhmm.split(":");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }
}
