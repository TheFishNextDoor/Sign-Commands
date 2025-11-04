package fun.sunrisemc.sign_commands.file;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.YamlConfiguration;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class ConfigFile {

    public static YamlConfiguration get(@NonNull String name, boolean copyMissingDefaults) {
        File configFile = new File(getFolder(), name + ".yml");
        if (!configFile.exists()) {
            create(name);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (config == null) {
            SignCommandsPlugin.logWarning("Failed to load configuration for " + name + ".yml. Using empty configuration.");
            config = new YamlConfiguration();
        }

        if (copyMissingDefaults) {
            if (copyDefaults(config, getDefault(name))) {
                save(name, config);
            }
        }

        return config;
    }

    public static boolean save(@NonNull String filename, @NonNull YamlConfiguration config) {
        File configFile = new File(getFolder(), filename + ".yml");
        try {
            config.save(configFile);
            return true;
        } catch (Exception e) {
            SignCommandsPlugin.logWarning("Failed to save configuration for " + filename + ".yml.");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean create(@NonNull String filename) {
        try {
            SignCommandsPlugin.getInstance().saveResource(filename + ".yml", false);
        } catch (Exception e) {
            SignCommandsPlugin.logWarning("Failed to save default configuration for " + filename + ".yml.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean moveKeyIfExists(@NonNull String filename, @NonNull String fromKey, @NonNull String toKey) {
        YamlConfiguration config = get(filename, false);
        if (config.contains(fromKey)) {
            config.set(toKey, config.get(fromKey));
            config.set(fromKey, null);
            save(filename, config);
            return true;
        }
        return false;
    }

    private static File getFolder() {
        File pluginFolder = SignCommandsPlugin.getInstance().getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        return pluginFolder;
    }

    @NonNull
    private static YamlConfiguration getDefault(@NonNull String name) {
        YamlConfiguration defaultConfig = new YamlConfiguration();
        try {
            InputStream resourceStream = SignCommandsPlugin.getInstance().getResource(name + ".yml");
            if (resourceStream != null) {
                InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
                defaultConfig.load(reader);
            }
        } catch (Exception e) {
            SignCommandsPlugin.logWarning("Failed to load default configuration for " + name + ".yml.");
            e.printStackTrace();
        }
        return defaultConfig;
    }

    private static boolean copyDefaults(@NonNull YamlConfiguration config, @NonNull YamlConfiguration defaultConfig) {
        boolean changed = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
                changed = true;
            }
        }
        return changed;
    }
}