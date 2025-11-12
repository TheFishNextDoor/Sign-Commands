package fun.sunrisemc.sign_commands.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

public class ConfigUtils {

    public static int getIntClamped(@NotNull YamlConfiguration config, @NotNull String path, int min, int max) {
        if (!config.contains(path)) {
            return min;
        }
        int value = config.getInt(path);
        return Math.clamp(value, min, max);
    }

    public static double getDoubleClamped(@NotNull YamlConfiguration config, @NotNull String path, double min, double max) {
        if (!config.contains(path)) {
            return min;
        }
        double value = config.getDouble(path);
        return Math.clamp(value, min, max);
    }

    public static long getLongClamped(@NotNull YamlConfiguration config, @NotNull String path, long min, long max) {
        if (!config.contains(path)) {
            return min;
        }
        long value = config.getLong(path);
        return Math.clamp(value, min, max);
    }
}