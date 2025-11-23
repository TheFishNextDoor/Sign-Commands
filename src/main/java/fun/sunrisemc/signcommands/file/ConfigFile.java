package fun.sunrisemc.signcommands.file;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.SignCommandsPlugin;

public class ConfigFile {

    @NotNull
    public static YamlConfiguration get(@NotNull String name, boolean copyMissingDefaults) {
        // Get the file
        File configFile = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist
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

        // Copy missing default values
        if (copyMissingDefaults) {
            // Get default configuration
            YamlConfiguration defaultConfig = new YamlConfiguration();
            try {
                InputStream resourceStream = SignCommandsPlugin.getInstance().getResource(name + ".yml");
                InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
                defaultConfig.load(reader);
            } 
            catch (Exception e) {
                SignCommandsPlugin.logSevere("Failed to get default configuration for " + name + ".yml.");
                return config;
            }

            // Copy missing keys
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

    public static boolean save(@NotNull String name, @NotNull YamlConfiguration config) {
        // Get the file
        File configFile = new File(getFolder(), name + ".yml");

        // Save the configuration
        try {
            config.save(configFile);
            return true;
        } 
        catch (Exception e) {
            SignCommandsPlugin.logSevere("Failed to save configuration file for " + name + ".yml.");
            return false;
        }
    }

    public static boolean delete(@NotNull String name) {
        // Get the player data folder
        File playerDataFolder = getFolder();
        
        // Get the file
        File configFile = new File(playerDataFolder, name + ".yml");

        // Check if the file exists
        if (!configFile.exists()) {
            return true;
        }

        // Attempt to delete the file
        try {
            return configFile.delete();
        }
        catch (Exception e) {
            SignCommandsPlugin.logSevere("Failed to delete configuration file for " + name + ".yml.");
            return false;
        }
    }

    @NotNull
    public static File getFolder() {
        // Get plugin folder
        File pluginFolder = SignCommandsPlugin.getInstance().getDataFolder();

        // Create folder if it does not exist
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }

        return pluginFolder;
    }

    @NotNull
    public static ArrayList<String> getNames() {
        // Get folder
        File folder = getFolder();
        
        // Get all yaml files in the folder
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return new ArrayList<>();
        }

        // Create list of names
        ArrayList<String> names = new ArrayList<>();
        for (File file : files) {
            // Get file name
            String nameWithExtension = file.getName();

            // Remove .yml extension
            String name = nameWithExtension.substring(0, nameWithExtension.length() - 4);

            // Add to list
            names.add(name);
        }

        return names;
    }
}