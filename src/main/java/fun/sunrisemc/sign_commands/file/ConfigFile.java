package fun.sunrisemc.sign_commands.file;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.YamlConfiguration;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class ConfigFile {

    // File Modification Helpers

    public static YamlConfiguration get(@NonNull String name, boolean copyMissingDefaults) {
        File configFile = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist using the default resource
        if (!configFile.exists()) {
            try {
                SignCommandsPlugin.getInstance().saveResource(name + ".yml", false);
            } 
            catch (Exception e) {
                SignCommandsPlugin.logWarning("Failed to create configuration file for " + name + ".yml.");
                e.printStackTrace();
                return new YamlConfiguration();
            }
        }

        // Load the configuration
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (config == null) {
            SignCommandsPlugin.logWarning("Failed to load configuration for " + name + ".yml. Using empty configuration.");
            config = new YamlConfiguration();
        }

        // Copy missing default values from the resource file
        if (copyMissingDefaults) {
            YamlConfiguration defaultConfig = new YamlConfiguration();
            try {
                InputStream resourceStream = SignCommandsPlugin.getInstance().getResource(name + ".yml");
                if (resourceStream != null) {
                    InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
                    defaultConfig.load(reader);
                }
            } 
            catch (Exception e) {
                SignCommandsPlugin.logWarning("Failed to get default configuration for " + name + ".yml.");
                e.printStackTrace();
                return config;
            }

            boolean changed = false;
            for (String key : defaultConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.get(key));
                    changed = true;
                }
            }

            if (changed) {
                SignCommandsPlugin.logInfo("Added missing default values to " + name + ".yml.");
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

    public static File getFolder() {
        File pluginFolder = SignCommandsPlugin.getInstance().getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        return pluginFolder;
    }
}