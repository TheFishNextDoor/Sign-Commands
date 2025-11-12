package fun.sunrisemc.sign_commands.file;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class DataFile {

    @NotNull
    public static YamlConfiguration get(@NotNull String name) {
        File dataFile = new File(getFolder(), name + ".yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            }
            catch (Exception e) {
                SignCommandsPlugin.logWarning("Failed to create data file for " + name + ".yml.");
                e.printStackTrace();
                return new YamlConfiguration();
            }
        }
        
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(dataFile);
        return yamlConfiguration;
    }

    public static boolean save(@NotNull String name, @NotNull YamlConfiguration data) {
        File dataFile = new File(getFolder(), name + ".yml");
        try {
            data.save(dataFile);
            return true;
        }
        catch (Exception e) {
            SignCommandsPlugin.logWarning("Failed to save data file for " + name + ".yml.");
            e.printStackTrace();
            return false;
        }
    }

    @NotNull
    public static File getFolder() {
        File pluginFolder = ConfigFile.getFolder();

        File dataFolder = new File(pluginFolder, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
    }
}