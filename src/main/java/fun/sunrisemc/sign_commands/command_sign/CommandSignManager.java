package fun.sunrisemc.sign_commands.command_sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.file.ConfigFile;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;
import fun.sunrisemc.sign_commands.utils.RayTrace;

public class CommandSignManager {

    private static HashMap<String, CommandSign> signConfigurationsMap = new HashMap<>();
    private static ArrayList<CommandSign> signConfigurationsList = new ArrayList<>();

    public static Optional<CommandSign> get(Location location) {
        String key = toKey(location);
        return Optional.ofNullable(signConfigurationsMap.get(key));
    }

    public static Optional<CommandSign> getLookingAt(@NonNull Player player) {
        Optional<Block> block = RayTrace.block(player);
        if (block.isEmpty()) {
            return Optional.empty();
        }
        Location blockLocation = block.get().getLocation();
        return get(blockLocation);
    }

    public static void addCommand(@NonNull Location location, @NonNull SignCommandType type, @NonNull String command) {
        SignCommand newCommand = new SignCommand(type, command);
        Optional<CommandSign> existingSign = get(location);
        if (existingSign.isPresent()) {
            existingSign.get().addCommand(newCommand);
        }
        else {
            CommandSign newSign = new CommandSign(location, newCommand);
            String key = toKey(location);
            signConfigurationsMap.put(key, newSign);
            signConfigurationsList.add(newSign);
        }
    }

    public static boolean removeCommand(@NonNull Location location, int index) {
        Optional<CommandSign> existingSign = get(location);
        if (existingSign.isEmpty()) {
            return false;
        }
        CommandSign sign = existingSign.get();
        boolean removed = sign.removeCommand(index);
        if (removed && sign.getCommands().isEmpty()) {
            String key = toKey(location);
            signConfigurationsMap.remove(key);
            signConfigurationsList.remove(sign);
        }
        return removed;
    }

    public static void loadSigns() {
        signConfigurationsMap.clear();

        YamlConfiguration config = ConfigFile.get("signs", false);
        for (String id : config.getKeys(false)) {
            CommandSign signConfiguration = new CommandSign(config, id);
            signConfigurationsMap.put(id, signConfiguration);
        }

        signConfigurationsList = new ArrayList<>(signConfigurationsMap.values());

        SignCommandsPlugin.logInfo("Loaded " + signConfigurationsMap.size() + " sign configurations.");
    }

    public static void saveSigns() {
        // Todo
    }

    private static String toKey(Location location) {
        return location.getWorld().getName() + ":" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}