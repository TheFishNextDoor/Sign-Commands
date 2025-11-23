package fun.sunrisemc.signcommands.file;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.SignCommandsPlugin;

public class DataFile {

    @NotNull
    public static YamlConfiguration get(@NotNull String name) {
        // Get the file
        File dataFile = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            }
            catch (Exception e) {
                SignCommandsPlugin.logSevere("Failed to create data file for " + name + ".yml.");
                return new YamlConfiguration();
            }
        }
        
        // Load the configuration
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);

        return yamlConfiguration;
    }

    public static boolean save(@NotNull String name, @NotNull YamlConfiguration data) {
        // Get the file
        File dataFile = new File(getFolder(), name + ".yml");

        // Save the configuration
        try {
            data.save(dataFile);
            return true;
        }
        catch (Exception e) {
            SignCommandsPlugin.logSevere("Failed to save data file for " + name + ".yml.");
            return false;
        }
    }

    public static boolean delete(@NotNull String name) {
        // Get the player data folder
        File dataFile = getFolder();
        
        // Get the file
        File playerDataFile = new File(dataFile, name + ".yml");

        // Check if the file exists
        if (!playerDataFile.exists()) {
            return true;
        }

        // Attempt to delete the file
        try {
            return playerDataFile.delete();
        }
        catch (Exception e) {
            SignCommandsPlugin.logSevere("Failed to delete data file for " + name + ".yml.");
            return false;
        }
    }

    @NotNull
    public static File getFolder() {
        // Get plugin folder
        File pluginFolder = ConfigFile.getFolder();

        // Get the data folder
        File dataFolder = new File(pluginFolder, "data");

        // Create the data folder if it does not exist
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
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