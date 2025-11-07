package fun.sunrisemc.sign_commands.utils;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

public class StringUtils {

    public static Optional<Integer> parseInteger(@NonNull String str) {
        try {
            int value = Integer.parseInt(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Double> parseDouble(@NonNull String str) {
        try {
            double value = Double.parseDouble(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Optional<Long> parseLong(@NonNull String str) {
        try {
            long value = Long.parseLong(str);
            return Optional.of(value);
        } 
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}