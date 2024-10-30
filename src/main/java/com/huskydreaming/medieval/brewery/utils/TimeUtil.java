package com.huskydreaming.medieval.brewery.utils;

import com.huskydreaming.medieval.brewery.data.Brewery;

public class TimeUtil {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static long timeDifference(Brewery brewery) {
        long systemTime = System.currentTimeMillis();
        long breweryTime = brewery.getTimeStamp();
        return breweryTime - systemTime;
    }

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

    public static String format(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder formattedTime = new StringBuilder();

        // Add hours only if they are > 0
        if (hours > 0) {
            formattedTime.append(hours).append("h ");
        }

        // Add minutes only if they are > 0 or if hours exist
        if (minutes > 0) {
            formattedTime.append(minutes).append("m ");
        }

        // Add seconds only if they are > 0 and no hours or minutes exist (e.g., 45s)
        if (seconds > 0 && hours == 0 && minutes == 0) {
            formattedTime.append(seconds).append("s ");
        }

        return formattedTime.toString().trim(); // Remove the trailing space
    }
}