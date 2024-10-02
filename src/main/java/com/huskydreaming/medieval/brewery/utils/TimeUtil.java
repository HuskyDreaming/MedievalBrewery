package com.huskydreaming.medieval.brewery.utils;

public class TimeUtil {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String convertTimeStamp(long milliseconds) {
        long ms = milliseconds;
        StringBuilder text = new StringBuilder();
        if (ms > DAY) {
            text.append(ms / DAY).append("d ");
            ms %= DAY;
        }
        if (ms > HOUR) {
            text.append(ms / HOUR).append("h ");
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            text.append(ms / MINUTE).append("m ");
            ms %= MINUTE;
        }
        if (ms > SECOND) {
            text.append(ms / SECOND).append("s ");
        }
        return text.toString();
    }
}
