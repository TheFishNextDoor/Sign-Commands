package fun.sunrisemc.sign_commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.command.SignCommands;

public class SignCommandsPlugin extends JavaPlugin {

    private static SignCommandsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        loadConfigs();

        registerCommand("signcommands", new SignCommands());

        logInfo("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        logInfo("Plugin disabled.");
    }

    public static SignCommandsPlugin getInstance() {
        return instance;
    }

    public static void loadConfigs() {
        // Load configuration files here
    }

    public static void logInfo(@NonNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NonNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NonNull String message) {
        getInstance().getLogger().severe(message);
    }

    private boolean registerCommand(@NonNull String commandName, @NonNull CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            logSevere("Command '" + commandName + "' not found in plugin.yml.");
            return false;
        }

        command.setExecutor(commandExecutor);

        if (commandExecutor instanceof TabCompleter) {
            command.setTabCompleter((TabCompleter) commandExecutor);
        }

        return true;
    }
}