package fun.sunrisemc.sign_commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.command.SignCommands;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.config.MainConfig;
import fun.sunrisemc.sign_commands.event.BlockBreak;
import fun.sunrisemc.sign_commands.event.BlockInteract;
import fun.sunrisemc.sign_commands.event.PlayerJoin;
import fun.sunrisemc.sign_commands.event.SignChange;
import fun.sunrisemc.sign_commands.repeating_task.AutoSaveTask;
import fun.sunrisemc.sign_commands.repeating_task.TickCounterTask;
import fun.sunrisemc.sign_commands.user.CommandSignUserManager;

public class SignCommandsPlugin extends JavaPlugin {

    private static SignCommandsPlugin instance;

    private static MainConfig mainConfig;

    @Override
    public void onEnable() {
        instance = this;

        load();

        registerCommand("signcommands", new SignCommands());

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new BlockBreak(), this);
        pluginManager.registerEvents(new BlockInteract(), this);
        pluginManager.registerEvents(new SignChange(), this);

        TickCounterTask.start();
        AutoSaveTask.start();

        logInfo("Plugin enabled.");
    }

    @Override
    public void onDisable() {
        TickCounterTask.stop();
        AutoSaveTask.stop();

        CommandSignManager.saveSigns();
        CommandSignUserManager.saveAll();

        logInfo("Plugin disabled.");
    }

    // Instances

    public static SignCommandsPlugin getInstance() {
        return instance;
    }

    public static MainConfig getMainConfig() {
        return mainConfig;
    }

    // Saving and Loading

    public static void reload() {
        save();
        load();
    }

    private static void save() {
        CommandSignManager.saveSigns();
        CommandSignUserManager.saveAll();
    }

    private static void load() {
        mainConfig = new MainConfig();
        CommandSignManager.loadSigns();
        CommandSignUserManager.loadOnline();
    }

    // Logging

    public static void logInfo(@NonNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NonNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NonNull String message) {
        getInstance().getLogger().severe(message);
    }

    // Command Registration

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