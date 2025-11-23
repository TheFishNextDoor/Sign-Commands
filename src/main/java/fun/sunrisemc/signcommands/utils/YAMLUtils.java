package fun.sunrisemc.signcommands.utils;

import java.util.HashSet;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

public class YAMLUtils {

    // Getting

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

    public static Optional<String> getString(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        String value = config.getString(path);

        return Optional.ofNullable(value);
    }

    @NotNull
    public static String getStringOrDefault(@NotNull YamlConfiguration config, @NotNull String path, @NotNull String defaultValue) {
        if (!config.contains(path)) {
            return defaultValue;
        }

        String value = config.getString(path);

        return value != null ? value : defaultValue;
    }

    @NotNull
    public static HashSet<String> getKeys(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return new HashSet<>();
        }

        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return new HashSet<>();
        }

        return new HashSet<>(section.getKeys(false));
    }

    // Modifying

    public static boolean renameKey(YamlConfiguration config, String oldKey, String newKey) {
        if (!config.contains(oldKey) || !config.contains(newKey)) {
            return false;
        }

        Object value = config.get(oldKey);
        config.set(newKey, value);
        config.set(oldKey, null);
        return true;
    }

    public static boolean moveKey(YamlConfiguration config, String oldKey, String newKey) {
        if (!config.contains(oldKey) || config.contains(newKey)) {
            return false;
        }

        Object value = config.get(oldKey);
        config.set(newKey, value);
        config.set(oldKey, null);
        return true;
    }
}