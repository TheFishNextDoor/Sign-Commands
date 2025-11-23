package fun.sunrisemc.signcommands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.sunrisemc.signcommands.command.SignCommandsCommand;
import fun.sunrisemc.signcommands.command.SignEditCommand;
import fun.sunrisemc.signcommands.config.MainConfig;
import fun.sunrisemc.signcommands.event.BlockBreak;
import fun.sunrisemc.signcommands.event.BlockInteract;
import fun.sunrisemc.signcommands.event.PlayerJoin;
import fun.sunrisemc.signcommands.event.SignChange;
import fun.sunrisemc.signcommands.hook.Vault;
import fun.sunrisemc.signcommands.scheduler.AutoSaveTask;
import fun.sunrisemc.signcommands.scheduler.TickCounterTask;
import fun.sunrisemc.signcommands.sign.CommandSignManager;
import fun.sunrisemc.signcommands.user.CommandSignUserManager;

public class SignCommandsPlugin extends JavaPlugin {

    private static @Nullable SignCommandsPlugin instance;

    private static @Nullable MainConfig mainConfig;

    @Override
    public void onEnable() {
        instance = this;

        if (Vault.hook(this)) {
            logInfo("Vault hooked.");
        } 
        else {
            logWarning("Vault not found. Economy and Permissions features will be disabled.");
        }

        load();

        registerCommand("signcommands", new SignCommandsCommand());
        registerCommand("signedit", new SignEditCommand());

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

        save();

        logInfo("Plugin disabled.");
    }

    // Instances

    @NotNull
    public static SignCommandsPlugin getInstance() {
        if (instance != null) {
            return instance;
        }
        else {
            throw new IllegalStateException("Plugin instance not initialized.");
        }
        
    }

    @NotNull
    public static MainConfig getMainConfig() {
        if (mainConfig != null) {
            return mainConfig;
        }
        throw new IllegalStateException("Main config not initialized.");
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

    public static void logInfo(@NotNull String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(@NotNull String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logSevere(@NotNull String message) {
        getInstance().getLogger().severe(message);
    }

    // Command Registration

    private boolean registerCommand(@NotNull String commandName, @NotNull CommandExecutor commandExecutor) {
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