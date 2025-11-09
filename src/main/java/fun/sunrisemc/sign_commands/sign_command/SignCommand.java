package fun.sunrisemc.sign_commands.sign_command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class SignCommand {

    private final SignClickType clickType;

    private final SignCommandType commandType;

    private final @NotNull String command;

    public SignCommand(@NotNull SignClickType clickType, @NotNull SignCommandType commandType, @NotNull String command) {
        this.clickType = clickType;
        this.commandType = commandType;
        this.command = command;
    }

    public SignClickType getClickType() {
        return clickType;
    }

    public SignCommandType getCommandType() {
        return commandType;
    }

    public @NotNull String getCommand() {
        return command;
    }

    public void execute(@NotNull Player player, @NotNull SignClickType clickType) {
        if (this.clickType != SignClickType.ANY_CLICK && this.clickType != clickType) {
            return;
        }

        String playerName = player.getName();
        String parsedCommand = command.replace("{player}", playerName);
        if (commandType == SignCommandType.PLAYER) {
            player.performCommand(parsedCommand);
        }
        else if (commandType == SignCommandType.CONSOLE) {
            Server server = Bukkit.getServer();
            ConsoleCommandSender consoleSender = server.getConsoleSender();
            server.dispatchCommand(consoleSender, parsedCommand);
        }
        else if (commandType == SignCommandType.MESSAGE) {
            String message = ChatColor.translateAlternateColorCodes('&', parsedCommand);
            player.sendMessage(message);
        }
        else if (commandType == SignCommandType.BROADCAST) {
            String message = ChatColor.translateAlternateColorCodes('&', parsedCommand);
            Bukkit.broadcastMessage(message);
        }
    }
}