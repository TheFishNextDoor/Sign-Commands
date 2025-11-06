package fun.sunrisemc.sign_commands.command_sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.file.ConfigFile;
import fun.sunrisemc.sign_commands.utils.RayTrace;

public class CommandSignManager {

    private static boolean savingSigns = false;

    private static HashMap<String, CommandSign> signConfigurationsIdsMap = new HashMap<>();
    private static HashMap<String, CommandSign> signConfigurationsLocationsMap = new HashMap<>();
    private static ArrayList<CommandSign> signConfigurationsList = new ArrayList<>();

    // Getting

    public static Optional<CommandSign> getByName(@NonNull String id) {
        return Optional.ofNullable(signConfigurationsIdsMap.get(id));
    }

    public static Optional<CommandSign> getAtLocation(@NonNull Location location) {
        String key = toKey(location);
        return Optional.ofNullable(signConfigurationsLocationsMap.get(key));
    }

    public static Optional<CommandSign> getLookingAt(@NonNull Player player) {
        Optional<Block> block = RayTrace.block(player);
        if (block.isEmpty()) {
            return Optional.empty();
        }

        Location blockLocation = block.get().getLocation();
        return getAtLocation(blockLocation);
    }

    public static ArrayList<String> getAllIds() {
        return new ArrayList<>(signConfigurationsIdsMap.keySet());
    }

    public static List<CommandSign> getAll() {
        return Collections.unmodifiableList(signConfigurationsList);
    }

    // Registering and Unregistering

    protected static void register(@NonNull CommandSign signCommand) {
        String name = signCommand.getName();
        signConfigurationsIdsMap.put(name, signCommand);

        Optional<Location> signLocation = signCommand.getSignLocation();
        if (!signLocation.isEmpty()) {
            String locationKey = toKey(signLocation.get());
            signConfigurationsLocationsMap.put(locationKey, signCommand);
        }
        
        signConfigurationsList.add(signCommand);
    }

    protected static void unregister(@NonNull CommandSign signCommand) {
        String signName = signCommand.getName();
        signConfigurationsIdsMap.remove(signName);

        Optional<Location> signLocation = signCommand.getSignLocation();
        if (!signLocation.isEmpty()) {
            String locationKey = toKey(signLocation.get());
            signConfigurationsLocationsMap.remove(locationKey);
        }

        signConfigurationsList.remove(signCommand);
    }
    
    // Loading and Saving

    public static void loadSigns() {
        SignCommandsPlugin.logInfo("Loading sign configurations...");
        
        signConfigurationsIdsMap.clear();
        signConfigurationsLocationsMap.clear();
        signConfigurationsList.clear();

        YamlConfiguration config = ConfigFile.get("signs", false);
        for (String name : config.getKeys(false)) {
            new CommandSign(config, name);
        }

        SignCommandsPlugin.logInfo("Loaded " + signConfigurationsLocationsMap.size() + " sign configurations.");
    }

    public static void saveSigns() {
        if (savingSigns) {
            return;
        }
        savingSigns = true;

        YamlConfiguration config = new YamlConfiguration();
        for (CommandSign signConfiguration : signConfigurationsList) {
            signConfiguration.saveTo(config);
        }

        ConfigFile.save("signs", config);

        savingSigns = false;
    }

    // Utils

    protected static String generateName() {
        int idx = 1;
        while (true) {
            String name = "sign-" + idx;
            if (!signConfigurationsIdsMap.containsKey(name)) {
                return name;
            }
            idx++;
        }
    }

    private static String toKey(@NonNull Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}