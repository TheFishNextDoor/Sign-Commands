package fun.sunrisemc.sign_commands.file;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class ConfigFile {

    @NotNull
    public static YamlConfiguration get(@NotNull String name, boolean copyMissingDefaults) {
        SignCommandsPlugin.logInfo("Loading configuration for " + name + ".yml.");

        File configFile = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist using the default resource
        if (!configFile.exists()) {
            try {
                SignCommandsPlugin.getInstance().saveResource(name + ".yml", false);
                SignCommandsPlugin.logInfo("Created configuration file for " + name + ".yml.");
            } 
            catch (Exception e) {
                SignCommandsPlugin.logWarning("Failed to create configuration file for " + name + ".yml.");
                e.printStackTrace();
                return new YamlConfiguration();
            }
        }

        // Load the configuration
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

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

    public static boolean save(@NotNull String filename, @NotNull YamlConfiguration config) {
        SignCommandsPlugin.logInfo("Saving configuration for " + filename + ".yml.");

        File configFile = new File(getFolder(), filename + ".yml");
        try {
            config.save(configFile);
            SignCommandsPlugin.logInfo("Configuration for " + filename + ".yml saved successfully.");
            return true;
        } 
        catch (Exception e) {
            SignCommandsPlugin.logWarning("Failed to save configuration for " + filename + ".yml.");
            e.printStackTrace();
            return false;
        }
    }

    @NotNull
    public static File getFolder() {
        File pluginFolder = SignCommandsPlugin.getInstance().getDataFolder();
        if (!pluginFolder.exists()) {
            SignCommandsPlugin.logInfo("Creating plugin folder.");
            pluginFolder.mkdirs();
        }
        return pluginFolder;
    }
}