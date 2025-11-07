package fun.sunrisemc.sign_commands.file;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class PlayerDataFile {

    public static YamlConfiguration get(@NonNull UUID uuid) {
        File playerDataFile = new File(getFolder(), uuid.toString() + ".yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            }
            catch (Exception e) {
                SignCommandsPlugin.logWarning("Failed to create player data file.");
                e.printStackTrace();
                return new YamlConfiguration();
            }
        }
        return YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public static boolean save(@NonNull UUID uuid, @NonNull YamlConfiguration data) {
        String name = uuid.toString();
        File playerDataFile = new File(getFolder(), name + ".yml");
        try {
            data.save(playerDataFile);
            return true;
        }
        catch (Exception e) {
            SignCommandsPlugin.logWarning("Failed to save player data file for " + name + ".yml.");
            e.printStackTrace();
            return false;
        }
    }

    public static File getFolder() {
        File dataFolder = DataFile.getFolder();

        File playerDataFolder = new File(dataFolder, "players");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }

        return playerDataFolder;
    }   
}