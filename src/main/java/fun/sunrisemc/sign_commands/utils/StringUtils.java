package fun.sunrisemc.sign_commands.utils;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class StringUtils {

    public static Optional<Integer> parseInteger(@NotNull String str) {
        try {
            int value = Integer.parseInt(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(@NotNull String str) {
        try {
            double value = Double.parseDouble(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(@NotNull String str) {
        try {
            long value = Long.parseLong(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public static String normalize(@NotNull String str) {
        return str.trim().toLowerCase().replace(" ", "-").replace("_", "-");
    }

    @NotNull
    public static String formatMillis(long millis) {
        if (millis < 1000) {
            if (millis == 1) {
                return millis + " millisecond";
            }
            else {
                return millis + " milliseconds";
            }
        } 
        else if (millis < 60000) {
            int seconds = (int) (millis / 1000);
            if (seconds == 1) {
                return seconds + " second";
            }
            else {
                return seconds + " seconds";
            }
        }
        else if (millis < 3600000) {
            int minutes = (int) (millis / 60000);
            if (minutes == 1) {
                return minutes + " minute";
            }
            else {
                return minutes + " minutes";
            }
        }
        else if (millis < 86400000) {
            int hours = (int) (millis / 3600000);
            if (hours == 1) {
                return hours + " hour";
            }
            else {
                return hours + " hours";
            }
        }
        else {
            int days = (int) (millis / 86400000);
            if (days == 1) {
                return days + " day";
            }
            else {
                return days + " days";
            }
        }
    }

    @NotNull
    public static ArrayList<String> getRangeStrings(int start, int end) {
        ArrayList<String> rangeStrings = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            rangeStrings.add(String.valueOf(i));
        }
        return rangeStrings;
    }
}