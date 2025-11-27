package fun.sunrisemc.signcommands.utils;

import java.util.HashSet;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

public class YAMLUtils {

    // Getting

    public static Optional<Boolean> getBoolean(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        boolean value = config.getBoolean(path);

        return Optional.of(value);
    }

    public static Optional<Integer> getInt(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        int value = config.getInt(path);

        return Optional.of(value);
    }

    public static Optional<Integer> getIntClamped(@NotNull YamlConfiguration config, @NotNull String path, int min, int max) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        int value = config.getInt(path);

        return Optional.of(Math.clamp(value, min, max));
    }

    public static Optional<Double> getDouble(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        double value = config.getDouble(path);

        return Optional.of(value);
    }

    public static Optional<Double> getDoubleClamped(@NotNull YamlConfiguration config, @NotNull String path, double min, double max) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        double value = config.getDouble(path);

        return Optional.of(Math.clamp(value, min, max));
    }

    public static Optional<Long> getLong(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        long value = config.getLong(path);

        return Optional.of(value);
    }

    public static Optional<Long> getLongClamped(@NotNull YamlConfiguration config, @NotNull String path, long min, long max) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        long value = config.getLong(path);

        return Optional.of(Math.clamp(value, min, max));
    }

    public static Optional<String> getString(@NotNull YamlConfiguration config, @NotNull String path) {
        if (!config.contains(path)) {
            return Optional.empty();
        }

        String value = config.getString(path);

        return Optional.ofNullable(value);
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