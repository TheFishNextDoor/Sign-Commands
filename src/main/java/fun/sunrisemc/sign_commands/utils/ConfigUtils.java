package fun.sunrisemc.sign_commands.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfigUtils {

    public static int getIntClamped(@NonNull YamlConfiguration config, @NonNull String path, int min, int max) {
        int value = config.getInt(path);
        return Math.clamp(value, min, max);
    }

    public static double getDoubleClamped(@NonNull YamlConfiguration config, @NonNull String path, double min, double max) {
        double value = config.getDouble(path);
        return Math.clamp(value, min, max);
    }

    public static long getLongClamped(@NonNull YamlConfiguration config, @NonNull String path, long min, long max) {
        long value = config.getLong(path);
        return Math.clamp(value, min, max);
    }
}