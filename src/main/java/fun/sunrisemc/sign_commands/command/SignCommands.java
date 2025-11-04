package fun.sunrisemc.sign_commands.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.config.MainConfig;
import fun.sunrisemc.sign_commands.permission.Permissions;
import fun.sunrisemc.sign_commands.sign_command.SignClickType;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;
import fun.sunrisemc.sign_commands.utils.RayTrace;
import net.md_5.bungee.api.ChatColor;

public class SignCommands implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }
        Player player = (Player) sender;
        
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<String>();
            completions.add("help");
            if (player.hasPermission(Permissions.RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (player.hasPermission(Permissions.ADD_PERMISSION)) {
                completions.add("add");
            }
            if (player.hasPermission(Permissions.REMOVE_PERMISSION)) {
                completions.add("remove");
            }
            if (player.hasPermission(Permissions.LIST_PERMISSION)) {
                completions.add("list");
            }
            if (player.hasPermission(Permissions.GOTO_PERMISSION)) {
                completions.add("goto");
            }
            return completions;
        }
        else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("add")) {
                return SignClickType.getNames();
            }
            else if (subcommand.equals("remove")) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
                if (commandSign.isEmpty()) {
                    return null;
                }
                ArrayList<SignCommand> commands = commandSign.get().getCommands();
                return getRangeStrings(0, commands.size() - 1);
            }
            else if (subcommand.equals("goto")) {
                return CommandSignManager.getAllIds();
            }
        }
        else if (args.length == 3) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("add")) {
                return SignCommandType.getNames();
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            helpMessage(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        // Reload Command
        if (player.hasPermission(Permissions.RELOAD_PERMISSION) && subcommand.equals("reload")) {
            SignCommandsPlugin.loadConfigs();
            player.sendMessage(ChatColor.GOLD + "Configuration reloaded.");
            return true;
        }
        // Add Command
        else if (player.hasPermission(Permissions.ADD_PERMISSION) && subcommand.equals("add")) {
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands add <clickType> <commandType> <command>");
                return true;
            }

            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }

            if (!checkIfValidBlock(targetBlock.get())) {
                player.sendMessage(ChatColor.RED + "You must be looking at a sign.");
                return true;
            }

            String clickTypeString = args[1];
            String commandTypeString = args[2];
            String commandString = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

            Optional<SignClickType> signClickType = SignClickType.fromName(clickTypeString);
            if (signClickType.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid click type. Valid types are: " + String.join(", ", SignClickType.getNames()));
                return true;
            }

            Optional<SignCommandType> signCommandType = SignCommandType.fromName(commandTypeString);
            if (signCommandType.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid command type. Valid types are: " + String.join(", ", SignCommandType.getNames()));
                return true;
            }



            Location location = targetBlock.get().getLocation();
            if (location == null || commandString.isEmpty()) {
                return true;
            }

            CommandSignManager.addCommand(location, signClickType.get(), signCommandType.get(), commandString);
            player.sendMessage(ChatColor.GOLD + "Command added.");
            return true;
        }
        // Remove Command
        else if (player.hasPermission(Permissions.REMOVE_PERMISSION) && subcommand.equals("remove")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands remove <index>");
                return true;
            }

            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            Location location = targetBlock.get().getLocation();
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            int index;
            try {
                index = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid command index.");
                return true;
            }

            if (location == null) {
                return true;
            }

            if (CommandSignManager.removeCommand(location, index)) {
                player.sendMessage(ChatColor.GOLD + "Command removed.");
            } else {
                player.sendMessage(ChatColor.RED + "Invalid command index.");
            }

            return true;
        }
        // List Command
        else if (player.hasPermission(Permissions.LIST_PERMISSION) && subcommand.equals("list")) {
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            Location location = targetBlock.get().getLocation();
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            ArrayList<SignCommand> commands = commandSign.get().getCommands();
            player.sendMessage(ChatColor.GOLD + "Sign Commands:");
            for (int i = 0; i < commands.size(); i++) {
                SignCommand cmd = commands.get(i);
                player.sendMessage(ChatColor.GOLD + "" + i + ": " + ChatColor.WHITE + cmd.getCommandType().name() + " : " + cmd.getCommand());
            }

            return true;
        }
        // Goto Command
        else if (player.hasPermission(Permissions.GOTO_PERMISSION) && subcommand.equals("goto")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands goto <id>");
                return true;
            }

            String id = args[1];
            Optional<CommandSign> commandSign = CommandSignManager.getById(id);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No sign found with ID: " + id);
                return true;
            }

            Optional<Location> location = commandSign.get().getSignLocation();
            if (location.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Sign has no location.");
                return true;
            }

            player.teleport(location.get());
            player.sendMessage(ChatColor.GOLD + "Teleported to sign with ID: " + id);
            return true;
        }

        helpMessage(player);
        return true;
    }

    private void helpMessage(@NonNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Sign Commands Help");
        sender.sendMessage(ChatColor.GOLD + "/signcommands help " + ChatColor.WHITE + "Show this help message");
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands reload " + ChatColor.WHITE + "Reload the plugin");
        }
        if (sender.hasPermission(Permissions.ADD_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands add <type> <command> " + ChatColor.WHITE + "Add a sign command");
        }
        if (sender.hasPermission(Permissions.REMOVE_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands remove <index> " + ChatColor.WHITE + "Remove a sign command");
        }
        if (sender.hasPermission(Permissions.LIST_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands list " + ChatColor.WHITE + "List sign commands");
        }
    }

    private static ArrayList<String> getRangeStrings(int start, int end) {
        ArrayList<String> rangeStrings = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            rangeStrings.add(String.valueOf(i));
        }
        return rangeStrings;
    }

    private static boolean checkIfValidBlock(Block block) {
        MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
        if (mainConfig.ONLY_ALLOW_SIGNS) {
            return checkIfSign(block);
        }
        else {
            return true;
        }
    }

    private static boolean checkIfSign(Block block) {
        BlockState state = block.getState();
        if (state == null) {
            return false;
        }
        return (state instanceof Sign);
    }
}