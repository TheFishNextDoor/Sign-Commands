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
import fun.sunrisemc.sign_commands.sign_command.SignClickType;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;
import fun.sunrisemc.sign_commands.utils.RayTrace;

public class CommandSignManager {

    private static boolean changes = false;

    private static boolean saving = false;

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

    // Editing

    public static void addCommand(@NonNull Location location, @NonNull SignClickType clickType, @NonNull SignCommandType commandType, @NonNull String command) {
        SignCommand newCommand = new SignCommand(clickType, commandType, command);

        Optional<CommandSign> existingSign = getAtLocation(location);
        if (existingSign.isPresent()) {
            existingSign.get().addCommand(newCommand);
        }
        else {
            CommandSign newSign = new CommandSign(location, newCommand);
            String signId = newSign.getName();
            register(signId, newSign);
        }

        changes = true;
    }

    public static boolean removeCommand(@NonNull Location location, int index) {
        Optional<CommandSign> existingCommandSign = getAtLocation(location);
        if (existingCommandSign.isEmpty()) {
            return false;
        }

        CommandSign commandSign = existingCommandSign.get();
        if (commandSign.removeCommand(index) && commandSign.getCommands().isEmpty()) {
            String key = toKey(location);
            signConfigurationsLocationsMap.remove(key);
            signConfigurationsList.remove(commandSign);
        }

        changes = true;

        return true;
    }

    public static boolean editCommand(@NonNull Location location, int index, @NonNull SignClickType clickType, @NonNull SignCommandType commandType, @NonNull String command) {
        Optional<CommandSign> existingCommandSign = getAtLocation(location);
        if (existingCommandSign.isEmpty()) {
            return false;
        }

        CommandSign commandSign = existingCommandSign.get();
        commandSign.editCommand(index, clickType, commandType, command);

        changes = true;
        
        return true;
    }

    public static boolean renameSign(@NonNull CommandSign commandSign, @NonNull String newId) {
        if (signConfigurationsIdsMap.containsKey(newId)) {
            return false;
        }

        String oldId = commandSign.getName();
        signConfigurationsIdsMap.remove(oldId);
        commandSign.setName(newId);
        signConfigurationsIdsMap.put(newId, commandSign);

        changes = true;

        return true;
    }

    public static boolean deleteSign(@NonNull CommandSign commandSign) {
        String name = commandSign.getName();

        Optional<Location> signLocation = commandSign.getSignLocation();
        if (signLocation.isPresent()) {
            String locationKey = toKey(signLocation.get());
            signConfigurationsLocationsMap.remove(locationKey);
        }

        signConfigurationsIdsMap.remove(name);
        signConfigurationsList.remove(commandSign);

        changes = true;

        return true;
    }
    
    // Loading and Saving

    public static void loadSigns() {
        SignCommandsPlugin.logInfo("Loading sign configurations...");
        
        signConfigurationsIdsMap.clear();
        signConfigurationsLocationsMap.clear();
        signConfigurationsList.clear();

        YamlConfiguration config = ConfigFile.get("signs", false);
        for (String name : config.getKeys(false)) {
            CommandSign signConfiguration = new CommandSign(config, name);
            register(name, signConfiguration);
        }

        changes = false;

        SignCommandsPlugin.logInfo("Loaded " + signConfigurationsLocationsMap.size() + " sign configurations.");
    }

    public static void saveSigns() {
        if (!changes || saving) {
            return;
        }

        saving = true;
        changes = false;

        SignCommandsPlugin.logInfo("Saving " + signConfigurationsList.size() + " sign configurations...");

        YamlConfiguration config = new YamlConfiguration();
        for (CommandSign signConfiguration : signConfigurationsList) {
            signConfiguration.saveTo(config);
        }

        ConfigFile.save("signs", config);
        
        SignCommandsPlugin.logInfo("Saved " + signConfigurationsList.size() + " sign configurations.");

        saving = false;
    }

    // Utils

    static String generateName() {
        int idx = 1;
        while (true) {
            String name = "sign-" + idx;
            if (!signConfigurationsIdsMap.containsKey(name)) {
                return name;
            }
            idx++;
        }
    }

    private static void register(@NonNull String name, @NonNull CommandSign signCommand) {
        signConfigurationsIdsMap.put(name, signCommand);
        Optional<Location> signLocation = signCommand.getSignLocation();
            if (!signLocation.isEmpty()) {
                String locationKey = toKey(signLocation.get());
                signConfigurationsLocationsMap.put(locationKey, signCommand);
            }
        signConfigurationsList.add(signCommand);
    }

    private static String toKey(@NonNull Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}