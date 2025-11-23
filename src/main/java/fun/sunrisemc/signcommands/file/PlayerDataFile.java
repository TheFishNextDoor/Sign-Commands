package fun.sunrisemc.signcommands.file;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.SignCommandsPlugin;

public class PlayerDataFile {

    @NotNull
    public static YamlConfiguration get(@NotNull UUID uuid) {
        // Get the name
        String name = uuid.toString();

        // Get the file
        File playerDataFile = new File(getFolder(), name + ".yml");

        // Create the file if it does not exist
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            }
            catch (Exception e) {
                SignCommandsPlugin.logSevere("Failed to create player data file for " + name + ".yml.");
                return new YamlConfiguration();
            }
        }

        // Load the configuration
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
        
        return yamlConfiguration;
    }

    public static boolean save(@NotNull UUID uuid, @NotNull YamlConfiguration data) {
        // Get the name
        String name = uuid.toString();

        // Get the file
        File playerDataFile = new File(getFolder(), name + ".yml");

        // Save the configuration
        try {
            data.save(playerDataFile);
            return true;
        }
        catch (Exception e) {
            SignCommandsPlugin.logSevere("Failed to save player data file for " + name + ".yml.");
            return false;
        }
    }

    public static boolean delete(@NotNull String name) {
        // Get the player data folder
        File playerDataFolder = getFolder();
        
        // Get the file
        File playerDataFile = new File(playerDataFolder, name + ".yml");

        // Check if the file exists
        if (!playerDataFile.exists()) {
            return true;
        }

        // Attempt to delete the file
        try {
            return playerDataFile.delete();
        }
        catch (Exception e) {
            SignCommandsPlugin.logSevere("Failed to delete player data file for " + name + ".yml.");
            return false;
        }
    }

    @NotNull
    public static File getFolder() {
        // Get data folder
        File dataFolder = DataFile.getFolder();

        // Get player data folder
        File playerDataFolder = new File(dataFolder, "players");

        // Create the player data folder if it does not exist
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
        
        return playerDataFolder;
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