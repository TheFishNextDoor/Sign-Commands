package fun.sunrisemc.sign_commands.file;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class DataFile {

    @NonNull
    public static YamlConfiguration get(@NonNull String name) {
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
        if (yamlConfiguration == null) {
            return new YamlConfiguration();
        }
        return yamlConfiguration;
    }

    public static boolean save(@NonNull String name, @NonNull YamlConfiguration data) {
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

    public static File getFolder() {
        File pluginFolder = ConfigFile.getFolder();

        File dataFolder = new File(pluginFolder, "data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        return dataFolder;
    }
}