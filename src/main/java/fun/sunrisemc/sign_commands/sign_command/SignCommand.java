package fun.sunrisemc.sign_commands.sign_command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.md_5.bungee.api.ChatColor;

public class SignCommand {

    private final SignClickType clickType;

    private final SignCommandType commandType;

    private final String command;

    public SignCommand(@NonNull SignClickType clickType, @NonNull SignCommandType commandType, @NonNull String command) {
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

    public String getCommand() {
        return command;
    }

    public void execute(@NonNull Player player, @NonNull SignClickType clickType) {
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