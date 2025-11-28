package fun.sunrisemc.signcommands.utils;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.NotNull;

public class StringUtils {

    // Parsing

    public static Optional<Integer> parseInteger(@NotNull String str) {
        try {
            int value = Integer.parseInt(str.trim());
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(@NotNull String str) {
        try {
            double value = Double.parseDouble(str.trim());
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(@NotNull String str) {
        try {
            long value = Long.parseLong(str.trim());
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Side> parseSide(@NotNull String sideNameB) {
        String sideNameBNormalized = StringUtils.normalize(sideNameB);
        for (Side side : Side.values()) {
            String sideNameANormalized = StringUtils.normalize(side.name());
            if (sideNameANormalized.equals(sideNameBNormalized)) {
                return Optional.of(side);
            }
        }
        return Optional.empty();
    }

    // Formatting

    @NotNull
    public static String formatName(@NotNull String name) {
        return name.toLowerCase()
                   .replace("minecraft:", "")
                   .replace(" ", "-")
                   .replace("_", "-")
                   .replace(":", "-")
                   .trim();
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

    // Normalization

    @NotNull
    public static String normalize(@NotNull String str) {
        return str.toLowerCase()
                  .replace("minecraft:", "")
                  .replace(" ", "")
                  .replace("_", "")
                  .replace("-", "")
                  .replace(":", "")
                  .trim();
    }

    // Range Strings

    @NotNull
    public static ArrayList<String> getRangeStrings(int start, int end) {
        ArrayList<String> rangeStrings = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            rangeStrings.add(String.valueOf(i));
        }
        return rangeStrings;
    }
}