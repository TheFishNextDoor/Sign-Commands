package fun.sunrisemc.sign_commands.sign_command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.md_5.bungee.api.ChatColor;

public class SignCommand {

    private final SignCommandType type;

    private final String command;

    public SignCommand(SignCommandType type, String command) {
        this.type = type;
        this.command = command;
    }

    public SignCommandType getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public void execute(@NonNull Player player) {
        String playerName = player.getName();
        String parsedCommand = command.replace("{player}", playerName);
        if (type == SignCommandType.PLAYER) {
            player.performCommand(parsedCommand);
        }
        else if (type == SignCommandType.CONSOLE) {
            Server server = Bukkit.getServer();
            ConsoleCommandSender consoleSender = server.getConsoleSender();
            server.dispatchCommand(consoleSender, parsedCommand);
        }
        else if (type == SignCommandType.MESSAGE) {
            String message = ChatColor.translateAlternateColorCodes('&', parsedCommand);
            player.sendMessage(message);
        }
        else if (type == SignCommandType.BROADCAST) {
            String message = ChatColor.translateAlternateColorCodes('&', parsedCommand);
            Bukkit.broadcastMessage(message);
        }
    }
}