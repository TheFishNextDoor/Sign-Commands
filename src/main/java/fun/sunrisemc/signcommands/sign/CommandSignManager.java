package fun.sunrisemc.signcommands.sign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.SignCommandsPlugin;
import fun.sunrisemc.signcommands.config.MainConfig;
import fun.sunrisemc.signcommands.file.DataFile;
import fun.sunrisemc.signcommands.utils.PlayerUtils;
import fun.sunrisemc.signcommands.utils.SignUtils;

public class CommandSignManager {

    private static @NotNull HashMap<String, CommandSign> signConfigurationsIdsMap = new HashMap<>();
    private static @NotNull HashMap<Location, CommandSign> signConfigurationsLocationsMap = new HashMap<>();
    private static @NotNull ArrayList<CommandSign> signConfigurationsList = new ArrayList<>();

    // Getting

    public static Optional<CommandSign> getByName(@NotNull String id) {
        return Optional.ofNullable(signConfigurationsIdsMap.get(id));
    }

    public static Optional<CommandSign> getByBlock(@NotNull Block block) {
        MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
        if (mainConfig.ONLY_ALLOW_SIGNS) {
            if (!SignUtils.isSign(block)) {
                return Optional.empty();
            }
        }
        Location location = block.getLocation();
        return Optional.ofNullable(signConfigurationsLocationsMap.get(location));
    }

    public static Optional<CommandSign> getOrCreateLookingAt(@NotNull Player player) {
        Optional<Block> block = PlayerUtils.getLookedAtBlock(player);
        if (block.isEmpty()) {
            return Optional.empty();
        }

        Optional<CommandSign> commandSign = getByBlock(block.get());
        if (!commandSign.isEmpty()) {
            return commandSign;
        }

        MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
        if (mainConfig.ONLY_ALLOW_SIGNS) {
            if (!SignUtils.isSign(block.get())) {
                return Optional.empty();
            }
        }

        Location blockLocation = block.get().getLocation();
        CommandSign newSign = new CommandSign(blockLocation);
        return Optional.of(newSign);
    }

    public static Optional<CommandSign> getLookingAt(@NotNull Player player) {
        Optional<Block> block = PlayerUtils.getLookedAtBlock(player);
        if (block.isEmpty()) {
            return Optional.empty();
        }

        return getByBlock(block.get());
    }

    @NotNull
    public static ArrayList<String> getAllIds() {
        return new ArrayList<>(signConfigurationsIdsMap.keySet());
    }

    @NotNull
    public static List<CommandSign> getAll() {
        return Collections.unmodifiableList(signConfigurationsList);
    }

    // Registering and Unregistering

    protected static void register(@NotNull CommandSign signCommand) {
        String name = signCommand.getName();
        signConfigurationsIdsMap.put(name, signCommand);

        Optional<Location> signLocation = signCommand.getSignLocation();
        if (!signLocation.isEmpty()) {
            Location locationKey = signLocation.get();
            signConfigurationsLocationsMap.put(locationKey, signCommand);
        }
        
        signConfigurationsList.add(signCommand);
    }

    protected static void unregister(@NotNull CommandSign signCommand) {
        String signName = signCommand.getName();
        signConfigurationsIdsMap.remove(signName);

        Optional<Location> signLocation = signCommand.getSignLocation();
        if (!signLocation.isEmpty()) {
            Location locationKey = signLocation.get();
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

        YamlConfiguration config = DataFile.get("signs");
        for (String name : config.getKeys(false)) {
            new CommandSign(config, name);
        }

        SignCommandsPlugin.logInfo("Loaded " + signConfigurationsLocationsMap.size() + " sign configurations.");
    }

    public static void saveSigns() {
        YamlConfiguration config = new YamlConfiguration();
        for (CommandSign signConfiguration : signConfigurationsList) {
            signConfiguration.saveTo(config);
        }

        DataFile.save("signs", config);
    }
}