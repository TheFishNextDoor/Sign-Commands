package fun.sunrisemc.sign_commands.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.permission.Permissions;
import fun.sunrisemc.sign_commands.sign_command.SignClickType;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;
import fun.sunrisemc.sign_commands.user.CommandSignUser;
import fun.sunrisemc.sign_commands.user.CommandSignUserManager;
import fun.sunrisemc.sign_commands.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class SignCommands implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        // /signcommands <subcommand>
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<String>();
            completions.add("help");
            if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (sender.hasPermission(Permissions.LIST_SIGNS_PERMISSION)) {
                completions.add("list");
            }
            if (sender.hasPermission(Permissions.INFO_PERMISSION)) {
                completions.add("info");
            }
            if (isPlayer && sender.hasPermission(Permissions.GOTO_SIGN_PERMISSION)) {
                completions.add("goto");
            }
            if (isPlayer && sender.hasPermission(Permissions.RENAME_SIGN_PERMISSION)) {
                completions.add("rename");
            }
            if (sender.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
                completions.add("delete");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                completions.add("addcommand");
                completions.add("removecommand");
                completions.add("editcommand");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
                completions.add("addrequiredpermission");
                completions.add("removerequiredpermission");
                completions.add("listrequiredpermissions");
                completions.add("addblockedpermission");
                completions.add("removeblockedpermission");
                completions.add("listblockedpermissions");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_COOLDOWN_PERMISSION)) {
                completions.add("setglobalclickcooldown");
                completions.add("resetglobalclickcooldown");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_LIMIT_PERMSSION)) {
                completions.add("setglobalclicklimit");
                completions.add("resetglobalclicklimit");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_USER_CLICK_COOLDOWN_PERMISSION)) {
                completions.add("setuserclickcooldown");
                completions.add("resetuserclickcooldown");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_USER_CLICK_LIMIT_PERMISSION)) {
                completions.add("setuserclicklimit");
                completions.add("resetuserclicklimit");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_CLICK_COST_PERMISSION)) {
                completions.add("setclickcost");
            }
            return completions;
        }
        else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            // /signcommands info <signName>
            if ((subcommand.equals("info") || subcommand.equals("i")) && sender.hasPermission(Permissions.INFO_PERMISSION)) {
                return CommandSignManager.getAllIds();
            }
            // /signcommands goto <signName>
            else if (isPlayer && (subcommand.equals("goto") || subcommand.equals("gt")) && sender.hasPermission(Permissions.GOTO_SIGN_PERMISSION)) {
                return CommandSignManager.getAllIds();
            }
            // /signcommands rename <newName>
            else if (isPlayer && (subcommand.equals("rename") || subcommand.equals("rn")) && sender.hasPermission(Permissions.RENAME_SIGN_PERMISSION)) {
                return Arrays.asList("<newName>");
            }
            // /signcommands delete <signName>
            else if (isPlayer && (subcommand.equals("delete") || subcommand.equals("dt")) && sender.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
                return CommandSignManager.getAllIds();
            }
            // /signcommands addcommand <clickType>
            else if (isPlayer && (subcommand.equals("addcommand") || subcommand.equals("ac")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                return SignClickType.getNames();
            }
            // /signcommands removecommand <commandIndex>
            else if (isPlayer && (subcommand.equals("removecommand") || subcommand.equals("rc")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                ArrayList<SignCommand> commands = commandSign.get().getCommands();
                return getRangeStrings(0, commands.size() - 1);
            }
            // /signcommands editcommand <commandIndex>
            else if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                ArrayList<SignCommand> commands = commandSign.get().getCommands();
                return getRangeStrings(0, commands.size() - 1);
            }
            // /signcommands addrequiredpermission <permission>
            else if (isPlayer && (subcommand.equals("addrequiredpermission") || subcommand.equals("arp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
                return Arrays.asList("<permission>");
            }
            // /signcommands removerequiredpermission <permission>
            else if (isPlayer && (subcommand.equals("removerequiredpermission") || subcommand.equals("rrp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                return commandSign.get().getRequiredPermissions().stream().toList();
            }
            // /signcommands addblockedpermission <permission>
            else if (isPlayer && (subcommand.equals("addblockedpermission") || subcommand.equals("abp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
                return Arrays.asList("<permission>");
            }
            // /signcommands removeblockedpermission <permission>
            else if (isPlayer && (subcommand.equals("removeblockedpermission") || subcommand.equals("rbp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                return commandSign.get().getBlockedPermissions().stream().toList();
            }
            // /signcommands setglobalclickcooldown <cooldownMilliseconds>
            else if (isPlayer && (subcommand.equals("setglobalclickcooldown") || subcommand.equals("sgcc")) && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_COOLDOWN_PERMISSION)) {
                return Arrays.asList("<cooldownMilliseconds>");
            }
            // /signcommands setglobalclicklimit <clickLimit>
            else if (isPlayer && (subcommand.equals("setglobalclicklimit") || subcommand.equals("sgcl")) && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_LIMIT_PERMSSION)) {
                return Arrays.asList("<clickLimit>");
            }
            // /signcommands setuserclickcooldown <cooldownMilliseconds>
            else if (isPlayer && (subcommand.equals("setuserclickcooldown") || subcommand.equals("succ")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_COOLDOWN_PERMISSION)) {
                return Arrays.asList("<cooldownMilliseconds>");
            }
            // /signcommands setuserclicklimit <clickLimit>
            else if (isPlayer && (subcommand.equals("setuserclicklimit") || subcommand.equals("sucl")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_LIMIT_PERMISSION)) {
                return Arrays.asList("<clickLimit>");
            }
            // /signcommands resetuserclicklimit <username>
            else if (isPlayer && (subcommand.equals("resetuserclicklimit") || subcommand.equals("rucl")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_LIMIT_PERMISSION)) {
                ArrayList<String> completions = getAllPlayerNames();
                completions.add("all");
                return completions;
            }
            // /signcommands resetuserclickcooldown <username>
            else if (isPlayer && (subcommand.equals("resetuserclickcooldown") || subcommand.equals("rucc")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_COOLDOWN_PERMISSION)) {
                ArrayList<String> completions = getAllPlayerNames();
                completions.add("all");
                return completions;
            }
            // /signcommands setclickcost <clickCost>
            else if (isPlayer && (subcommand.equals("setclickcost") || subcommand.equals("scc")) && sender.hasPermission(Permissions.MANAGE_CLICK_COST_PERMISSION)) {
                return Arrays.asList("<clickCost>");
            }
        }
        else if (args.length == 3) {
            String subcommand = args[0].toLowerCase();

            // /signcommands addcommand <clickType> <commandType>
            if (isPlayer && (subcommand.equals("addcommand") || subcommand.equals("ac")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                return SignCommandType.getNames();
            }
            // /signcommands editcommand <commandIndex> <clickType>
            else if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                return SignClickType.getNames();
            }
        }
        else if (args.length == 4) {
            String subcommand = args[0].toLowerCase();

            // /signcommands addcommand <clickType> <commandType> <command>
            if (isPlayer && (subcommand.equals("addcommand") || subcommand.equals("ac")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                Optional<SignCommandType> signCommandTypeOptional = SignCommandType.fromName(args[2]);
                if (signCommandTypeOptional.isEmpty()) {
                    return null;
                }

                SignCommandType signCommandType = signCommandTypeOptional.get();
                if (signCommandType == SignCommandType.CONSOLE || signCommandType == SignCommandType.PLAYER) {
                    return Arrays.asList("<command>");
                }
                else if (signCommandType == SignCommandType.MESSAGE || signCommandType == SignCommandType.BROADCAST) {
                    return Arrays.asList("<message>");
                }
            }
            // /signcommands editcommand <commandIndex> <clickType> <commandType>
            if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                return SignCommandType.getNames();
            }
        }
        else if (args.length == 5) {
            String subcommand = args[0].toLowerCase();

            // /signcommands editcommand <commandIndex> <clickType> <commandType> <command>
            if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                ArrayList<SignCommand> commands = commandSign.get().getCommands();
                String indexString = args[1];

                Optional<Integer> index = StringUtils.parseInteger(indexString);
                if (index.isEmpty()) {
                    return null;
                }

                if (index.get() < 0 || index.get() >= commands.size()) {
                    return null;
                }

                SignCommand signCommand = commands.get(index.get());
                String currentCommand = signCommand.getCommand();
                return Arrays.asList(currentCommand);
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (args.length == 0) {
            helpMessage(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        // Reload
        if ((subcommand.equals("reload") || subcommand.equals("rl")) && sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            SignCommandsPlugin.reload();
            sender.sendMessage(ChatColor.GOLD + "Configuration reloaded.");
            return true;
        }
        // List Signs
        else if ((subcommand.equals("list") || subcommand.equals("l")) && sender.hasPermission(Permissions.LIST_SIGNS_PERMISSION)) {
            // Check if there are any command signs
            List<CommandSign> commandSigns = CommandSignManager.getAll();
            if (commandSigns.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No command signs found.");
                return true;
            }

            // List the command signs
            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Command Signs");
            for (CommandSign sign : commandSigns) {
                Optional<Location> signLocation = sign.getSignLocation();
                if (signLocation.isEmpty()) {
                    continue;
                }

                Location location = signLocation.get();
                String locationString = locationString(location);
                int commandCount = sign.getCommands().size();
                if (commandCount == 1) {
                    sender.sendMessage(ChatColor.GOLD + sign.getName() + " " + locationString + " (" + commandCount + " command)");
                } 
                else {
                    sender.sendMessage(ChatColor.GOLD + sign.getName() + " " + locationString + " (" + commandCount + " commands)");
                }
            }

            return true;
        }
        // Info
        else if (isPlayer && (subcommand.equals("info") || subcommand.equals("i")) && sender.hasPermission(Permissions.INFO_PERMISSION)) {
            Player player = (Player) sender;

            // Lookup command sign by id if one is provided else get the one the player is looking at
            Optional<CommandSign> commandSignOptional;
            if (args.length >= 2) {
                commandSignOptional = CommandSignManager.getByName(args[1]);
            } 
            else {
                commandSignOptional = CommandSignManager.getLookingAt(player);
            }

            if (commandSignOptional.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Command sign not found.");
                return true;
            }
            CommandSign commandSign = commandSignOptional.get();

            // Display the command sign info
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + commandSign.getName());

            // Location
            Optional<Location> signLocation = commandSign.getSignLocation();
            if (signLocation.isPresent()) {
                player.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.WHITE + locationString(signLocation.get()));
            }

            // Required Permissions
            HashSet<String> requiredPermissions = commandSign.getRequiredPermissions();
            if (!requiredPermissions.isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "Required Permissions:");
                for (String permission : requiredPermissions) {
                    player.sendMessage(ChatColor.GOLD + "- " + permission);
                }
            }

            // Blocked Permissions
            HashSet<String> blockedPermissions = commandSign.getBlockedPermissions();
            if (!blockedPermissions.isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "Blocked Permissions:");
                for (String permission : blockedPermissions) {
                    player.sendMessage(ChatColor.GOLD + "- " + permission);
                }
            }

            // Global Click Cooldown
            long globalClickCooldownMillis = commandSign.getGlobalClickCooldownMillis();
            if (globalClickCooldownMillis > 0) {
                player.sendMessage(ChatColor.GOLD + "Global Click Cooldown: " + ChatColor.WHITE + StringUtils.formatMillis(globalClickCooldownMillis));
            }

            // Global Max Clicks
            int globalMaxClicks = commandSign.getGlobalMaxClicks();
            if (globalMaxClicks > 0) {
                player.sendMessage(ChatColor.GOLD + "Global Max Clicks: " + ChatColor.WHITE + globalMaxClicks);
            }

            // User Click Cooldown
            long userClickCooldownMillis = commandSign.getUserClickCooldownMillis();
            if (userClickCooldownMillis > 0) {
                player.sendMessage(ChatColor.GOLD + "User Click Cooldown: " + ChatColor.WHITE + StringUtils.formatMillis(userClickCooldownMillis));
            }

            // User Max Clicks
            int userMaxClicks = commandSign.getUserMaxClicks();
            if (userMaxClicks > 0) {
                player.sendMessage(ChatColor.GOLD + "User Max Clicks: " + ChatColor.WHITE + userMaxClicks);
            }

            // Click Cost
            double clickCost = commandSign.getClickCost();
            if (clickCost > 0) {
                player.sendMessage(ChatColor.GOLD + "Click Cost: " + ChatColor.WHITE + clickCost);
            }
            
            // List Commands
            ArrayList<SignCommand> signCommands = commandSign.getCommands();
            player.sendMessage(ChatColor.GOLD + "Commands:");
            for (int i = 0; i < signCommands.size(); i++) {
                SignCommand signCommand = signCommands.get(i);
                player.sendMessage(ChatColor.GOLD + "" + i + ". " + ChatColor.WHITE + signCommand.getCommandType().getName() + ": /" + signCommand.getCommand());
            }
            
            return true;
        }
        // Goto
        else if (isPlayer && (subcommand.equals("goto") || subcommand.equals("gt")) && sender.hasPermission(Permissions.GOTO_SIGN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <goto|gt> <signName>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getByName(args[1]);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Sign does not exist.");
                return true;
            }

            // Get the sign location
            Optional<Location> location = commandSign.get().getSignLocation();
            if (location.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Sign has no location.");
                return true;
            }

            // Teleport the player to the sign location
            player.teleport(location.get());
            player.sendMessage(ChatColor.GOLD + "Teleported to sign.");
            return true;
        }
        // Rename
        else if (isPlayer && (subcommand.equals("rename") || subcommand.equals("rn")) && sender.hasPermission(Permissions.RENAME_SIGN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <rename|rn> <newName>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Check if the new name is already taken
            String newName = args[1];
            if (CommandSignManager.getByName(newName).isPresent()) {
                player.sendMessage(ChatColor.RED + "A sign with that name already exists.");
                return true;
            }

            // Rename the sign
            commandSign.get().setName(newName);
            player.sendMessage(ChatColor.GOLD + "Command sign renamed to: " + newName);
            return true;
        }
        // Delete
        else if ((subcommand.equals("delete") || subcommand.equals("dt")) && sender.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
            // Check if the sender provided enough arguments
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /signcommands <delete|dt> <signName>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getByName(args[1]);
            if (commandSign.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Sign does not exist.");
                return true;
            }

            // Delete the sign
            commandSign.get().delete();
            sender.sendMessage(ChatColor.GOLD + "Command sign deleted.");
            return true;

        }
        // Add Command
        else if (isPlayer && (subcommand.equals("addcommand") || subcommand.equals("ac")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <addcommand|ac> <clickType> <commandType> <command>");
                return true;
            }

            // Parse the click type
            Optional<SignClickType> signClickType = SignClickType.fromName(args[1]);
            if (signClickType.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid click type. Valid types are: " + String.join(", ", SignClickType.getNames()));
                return true;
            }

            // Parse the command type
            Optional<SignCommandType> signCommandType = SignCommandType.fromName(args[2]);
            if (signCommandType.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid command type. Valid types are: " + String.join(", ", SignCommandType.getNames()));
                return true;
            }

            // Get or create the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getOrCreateLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a valid block.");
                return true;
            }

            // Add the command to the command sign
            String signCommandString = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            SignCommand signCommand = new SignCommand(signClickType.get(), signCommandType.get(), signCommandString);
            commandSign.get().addCommand(signCommand);
            player.sendMessage(ChatColor.GOLD + "Command added.");
            return true;
        }
        // Remove Command
        else if (isPlayer && (subcommand.equals("removecommand") || subcommand.equals("rc")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <removecommand|rc> <index>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            // Parse the command index
            Optional<Integer> index = StringUtils.parseInteger(args[1]);
            if (index.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid command index.");
                return true;
            }

            // Check if the index is valid
            ArrayList<SignCommand> commands = commandSign.get().getCommands();
            if (index.get() < 0 || index.get() >= commands.size()) {
                player.sendMessage(ChatColor.RED + "Invalid command index.");
                return true;
            }

            // Remove the command
            if (commandSign.get().removeCommand(index.get())) {
                player.sendMessage(ChatColor.GOLD + "Command removed.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "Failed to remove command.");
            }

            return true;
        }
        // Edit Command
        else if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 5) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <editcommand|ec> <commandIndex> <clickType> <commandType> <command>");
                return true;
            }

            // Get the command sign at that location
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            // Parse the command index
            Optional<Integer> index = StringUtils.parseInteger(args[1]);
            if (index.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid command index.");
                return true;
            }

            // Check if the index is valid
            ArrayList<SignCommand> commands = commandSign.get().getCommands();
            if (index.get() < 0 || index.get() >= commands.size()) {
                player.sendMessage(ChatColor.RED + "Invalid command index.");
                return true;
            }

            // Parse the click type
            Optional<SignClickType> signClickType = SignClickType.fromName(args[2]);
            if (signClickType.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid click type. Valid types are: " + String.join(", ", SignClickType.getNames()));
                return true;
            }

            // Parse the command type
            Optional<SignCommandType> signCommandType = SignCommandType.fromName(args[3]);
            if (signCommandType.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid command type. Valid types are: " + String.join(", ", SignCommandType.getNames()));
                return true;
            }

            // Edit the command
            String newSignCommandString = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            SignCommand newSignCommand = new SignCommand(signClickType.get(), signCommandType.get(), newSignCommandString);
            if (commandSign.get().editCommand(index.get(), newSignCommand)) {
                player.sendMessage(ChatColor.GOLD + "Ccommand edited.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "Failed to edit command.");
            }

            return true;
        }
        // Add Required Permission
        else if (isPlayer && (subcommand.equals("addrequiredpermission") || subcommand.equals("arp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <addrequiredpermission|arp> <permission>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Add the required permission
            if (commandSign.get().addRequiredPermission(args[1])) {
                player.sendMessage(ChatColor.GOLD + "Required permission added.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "That permission is already required.");
            }

            return true;
        }
        // Remove Required Permission
        else if (isPlayer && (subcommand.equals("removerequiredpermission") || subcommand.equals("rrp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <removerequiredpermission|rrp> <permission>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Remove the required permission
            if (commandSign.get().removeRequiredPermission(args[1])) {
                player.sendMessage(ChatColor.GOLD + "Required permission removed.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "That permission is not required.");
            }

            return true;
        }
        // List Required Permissions
        else if (isPlayer && (subcommand.equals("listrequiredpermissions") || subcommand.equals("lrp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // List the required permissions
            HashSet<String> requiredPermissions = commandSign.get().getRequiredPermissions();
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Required Permissions");
            if (requiredPermissions.isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "None");
            } 
            else {
                for (String permission : requiredPermissions) {
                    player.sendMessage(ChatColor.GOLD + "- " + permission);
                }
            }

            return true;
        }
        // Add Blocked Permission
        else if (isPlayer && (subcommand.equals("addblockedpermission") || subcommand.equals("abp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <addblockedpermission|abp> <permission>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Add the blocked permission
            if (commandSign.get().addBlockedPermission(args[1])) {
                player.sendMessage(ChatColor.GOLD + "Blocked permission added.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "That permission is already blocked.");
            }

            return true;
        }
        // Remove Blocked Permission
        else if (isPlayer && (subcommand.equals("removeblockedpermission") || subcommand.equals("rbp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <removeblockedpermission|rbp> <permission>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Remove the blocked permission
            if (commandSign.get().removeBlockedPermission(args[1])) {
                player.sendMessage(ChatColor.GOLD + "Blocked permission removed.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "That permission is not blocked.");
            }

            return true;
        }
        // List Blocked Permissions
        else if (isPlayer && (subcommand.equals("listblockedpermissions") || subcommand.equals("lbp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // List the blocked permissions
            HashSet<String> blockedPermissions = commandSign.get().getBlockedPermissions();
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Blocked Permissions");
            if (blockedPermissions.isEmpty()) {
                player.sendMessage(ChatColor.GOLD + "None");
            } 
            else {
                for (String permission : blockedPermissions) {
                    player.sendMessage(ChatColor.GOLD + "- " + permission);
                }
            }

            return true;
        }
        // set Global Click Cooldown Millis
        else if (isPlayer && (subcommand.equals("setglobalclickcooldown") || subcommand.equals("sgcc")) && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_COOLDOWN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setglobalclickcooldown|sgcc> <cooldownMilliseconds>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Parse the cooldown
            Optional<Long> cooldown = StringUtils.parseLong(args[1]);
            if (cooldown.isEmpty() || cooldown.get() < 0) {
                player.sendMessage(ChatColor.RED + "Invalid cooldown value.");
                return true;
            }

            // Set the cooldown
            commandSign.get().setGlobalClickCooldownMillis(cooldown.get());
            player.sendMessage(ChatColor.GOLD + "Cooldown set to: " + cooldown.get() + " milliseconds.");
            return true;
        }

        // Reset Global Click Cooldown Millis
        else if (isPlayer && (subcommand.equals("resetglobalclickcooldown") || subcommand.equals("rgcc")) && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_COOLDOWN_PERMISSION)) {
            Player player = (Player) sender;

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Reset the cooldown
            commandSign.get().resetGlobalClickCooldown();
            player.sendMessage(ChatColor.GOLD + "Global click cooldown reset.");
            return true;
        }
        // Set Global Click Limit
        else if (isPlayer && (subcommand.equals("setglobalclicklimit") || subcommand.equals("sgcl")) && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_LIMIT_PERMSSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setglobalclicklimit|sgcl> <clickLimit>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Parse the click limit
            Optional<Integer> maxClicks = StringUtils.parseInteger(args[1]);
            if (maxClicks.isEmpty() || maxClicks.get() < 0) {
                player.sendMessage(ChatColor.RED + "Invalid click limit value.");
                return true;
            }

            // Set the click limit
            commandSign.get().setGlobalMaxClicks(maxClicks.get());
            player.sendMessage(ChatColor.GOLD + "Click limit set to: " + maxClicks.get() + ".");
            return true;
        }
        // Reset Global Click Limit
        else if (isPlayer && (subcommand.equals("resetglobalclicklimit") || subcommand.equals("rgcl")) && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_LIMIT_PERMSSION)) {
            Player player = (Player) sender;

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Reset the click limit
            commandSign.get().resetGlobalClickLimit();
            player.sendMessage(ChatColor.GOLD + "Global click limit reset.");
            return true;
        }
        // Set User Click Cooldown Millis
        else if (isPlayer && (subcommand.equals("setuserclickcooldown") || subcommand.equals("succ")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_COOLDOWN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setuserclickcooldown|succ> <cooldownMilliseconds>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Parse the cooldown
            Optional<Long> cooldown = StringUtils.parseLong(args[1]);
            if (cooldown.isEmpty() || cooldown.get() < 0) {
                player.sendMessage(ChatColor.RED + "Invalid cooldown value.");
                return true;
            }

            // Set the cooldown
            commandSign.get().setUserClickCooldownMillis(cooldown.get());
            player.sendMessage(ChatColor.GOLD + "Cooldown set to: " + cooldown.get() + " milliseconds.");
            return true;
        }
        // Reset User Click Cooldown Millis
        else if (isPlayer && (subcommand.equals("resetuserclickcooldown") || subcommand.equals("rucc")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_COOLDOWN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <resetuserclickcooldown|rucc> <player | all>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Reset for all players if specified
            String target = args[1];
            if (target.equalsIgnoreCase("all")) {
                commandSign.get().resetAllUserClickCooldowns();
                player.sendMessage(ChatColor.GOLD + "All user click cooldowns reset.");
                return true;
            }

            // Get the target player
            Optional<Player> targetPlayer = getPlayerByName(target);
            if (targetPlayer.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            // Reset for the specific player
            CommandSignUser commandSignUser = CommandSignUserManager.get(targetPlayer.get());
            commandSignUser.resetLastSignClickTimeMillis(commandSign.get());
            sender.sendMessage(ChatColor.GOLD + "User click cooldown reset for " + targetPlayer.get().getName() + ".");
            return true;
        }
        // Set User Click Limit
        else if (isPlayer && (subcommand.equals("setuserclicklimit") || subcommand.equals("sucl")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_LIMIT_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setuserclicklimit|sucl> <clickLimit>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Parse the max clicks
            Optional<Integer> maxClicks = StringUtils.parseInteger(args[1]);
            if (maxClicks.isEmpty() || maxClicks.get() < 0) {
                player.sendMessage(ChatColor.RED + "Invalid click limit value.");
                return true;
            }

            // Set the max clicks
            commandSign.get().setUserMaxClicks(maxClicks.get());
            player.sendMessage(ChatColor.GOLD + "Click limit set to: " + maxClicks.get() + ".");
            return true;
        }
        // Reset User Click Limit
        else if (isPlayer && (subcommand.equals("resetuserclicklimit") || subcommand.equals("rucl")) && sender.hasPermission(Permissions.MANAGE_USER_CLICK_LIMIT_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <resetuserclicklimit|rucl> <player | all>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Reset for all players if specified
            String target = args[1];
            if (target.equalsIgnoreCase("all")) {
                commandSign.get().resetAllUserClickLimits();
                player.sendMessage(ChatColor.GOLD + "All user click limits reset.");
                return true;
            }

            // Get the target player
            Optional<Player> targetPlayer = getPlayerByName(target);
            if (targetPlayer.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            // Reset for the specific player
            CommandSignUser commandSignUser = CommandSignUserManager.get(targetPlayer.get());
            commandSignUser.resetSignClickCount(commandSign.get());
            sender.sendMessage(ChatColor.GOLD + "User click limit reset for " + targetPlayer.get().getName() + ".");
            return true;
        }
        // Set Click Cost
        else if (isPlayer && (subcommand.equals("setclickcost") || subcommand.equals("scc")) && sender.hasPermission(Permissions.MANAGE_CLICK_COST_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setclickcost|scc> <cost>");
                return true;
            }

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getLookingAt(player);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Parse the cost
            Optional<Double> cost = StringUtils.parseDouble(args[1]);
            if (cost.isEmpty() || cost.get() < 0) {
                player.sendMessage(ChatColor.RED + "Invalid cost value.");
                return true;
            }

            // Set the click cost
            commandSign.get().setClickCost(cost.get());
            player.sendMessage(ChatColor.GOLD + "Click cost set to: " + cost.get() + ".");
            return true;
        }

        // Help Message
        helpMessage(sender);
        return true;
    }

    private void helpMessage(@NonNull CommandSender sender) {
        boolean isPlayer = sender instanceof Player;

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Sign Commands Help");
        sender.sendMessage(ChatColor.GOLD + "/signcommands help " + ChatColor.WHITE + "Show this help message.");
        if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <reload | rl> " + ChatColor.WHITE + "Reload the plugin.");
        }
        if (sender.hasPermission(Permissions.LIST_SIGNS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <list | l> " + ChatColor.WHITE + "List all command signs.");
        }
        if (isPlayer && sender.hasPermission(Permissions.INFO_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <info | i> <signName> " + ChatColor.WHITE + "List a command sign's commands and other information.");
        }
        if (isPlayer && sender.hasPermission(Permissions.GOTO_SIGN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <goto | gt> <signName> " + ChatColor.WHITE + "Teleport to a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.RENAME_SIGN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <rename | rn> <newSignName> " + ChatColor.WHITE + "Rename a command sign. This will reset the sign's cooldown and max clicks.");
        }
        if (isPlayer && sender.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <delete | dt> <signName>" + ChatColor.WHITE + "Delete all commands from a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <addcommand | ac> <clickType> <commandType> <command> " + ChatColor.WHITE + "Add a command to a command sign.");
             sender.sendMessage(ChatColor.GOLD + "/signcommands <removecommand | rc> <commandIndex> " + ChatColor.WHITE + "Remove a command from a command sign.");
             sender.sendMessage(ChatColor.GOLD + "/signcommands <editcommand | ec> <commandIndex> <clickType> <commandType> <command> " + ChatColor.WHITE + "Edit a command on a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <addrequiredpermission | arp> <permission> " + ChatColor.WHITE + "Add a required permission to a command sign. A player must have all required permissions to use the sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <removerequiredpermission | rrp> <permission> " + ChatColor.WHITE + "Remove a required permission from a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <listrequiredpermissions | lrp> " + ChatColor.WHITE + "List required permissions of a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <addblockedpermission | abp> <permission> " + ChatColor.WHITE + "Add a blocked permission to a command sign. If a player has any of these permissions they will not be able to use the sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <removeblockedpermission | rbp> <permission> " + ChatColor.WHITE + "Remove a blocked permission from a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <listblockedpermissions | lbp> " + ChatColor.WHITE + "List blocked permissions of a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_COOLDOWN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setglobalclickcooldown | sgcc> <cooldownMilliseconds> " + ChatColor.WHITE + "Set the global click cooldown milliseconds for a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <resetglobalclickcooldown | rgcc> " + ChatColor.WHITE + "Reset the global click cooldown for a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_GLOBAL_CLICK_LIMIT_PERMSSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setglobalclicklimit | sgcl> <clickLimit> " + ChatColor.WHITE + "Set the global click limit for a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <resetglobalclicklimit | rgcl> " + ChatColor.WHITE + "Reset the global click limit for a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_USER_CLICK_COOLDOWN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setuserclickcooldown | succ> <cooldownMilliseconds> " + ChatColor.WHITE + "Set the user click cooldown milliseconds for a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <resetuserclickcooldown | rucc> <player | all> " + ChatColor.WHITE + "Reset the user click cooldown for all users for a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_USER_CLICK_LIMIT_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setuserclicklimit | sucl> <clickLimit> " + ChatColor.WHITE + "Set the user click limit for a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <resetuserclicklimit | rucl> <player | all> " + ChatColor.WHITE + "Reset the user click limit for all users for a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_CLICK_COST_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setclickcost | scc> <clickCost> " + ChatColor.WHITE + "Set the click cost for a command sign.");
        }
    }

    private static ArrayList<String> getRangeStrings(int start, int end) {
        ArrayList<String> rangeStrings = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            rangeStrings.add(String.valueOf(i));
        }
        return rangeStrings;
    }

    private static Optional<Player> getPlayerByName(String name) {
        Player player = org.bukkit.Bukkit.getPlayerExact(name);
        return Optional.ofNullable(player);
    }

    private static ArrayList<String> getAllPlayerNames() {
        ArrayList<String> playerNames = new ArrayList<String>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    private static String locationString(Location location) {
        return "(" + location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }
}