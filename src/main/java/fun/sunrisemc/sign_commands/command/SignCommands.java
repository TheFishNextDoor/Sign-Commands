package fun.sunrisemc.sign_commands.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
import fun.sunrisemc.sign_commands.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class SignCommands implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<String>();
            completions.add("help");
            if (sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (sender.hasPermission(Permissions.LIST_SIGNS_PERMISSION)) {
                completions.add("listsigns");
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
                completions.add("listcommands");
            }
            if (isPlayer && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
                completions.add("addrequiredpermission");
                completions.add("removerequiredpermission");
                completions.add("listrequiredpermission");
                completions.add("addblockedpermission");
                completions.add("removeblockedpermission");
                completions.add("listblockedpermission");
            }
            if (isPlayer && sender.hasPermission(Permissions.SET_COOLDOWN_PERMISSION)) {
                completions.add("setcooldown");
            }
            if (isPlayer && sender.hasPermission(Permissions.SET_MAX_CLICKS_PERMISSION)) {
                completions.add("setmaxclicksperuser");
            }
            return completions;
        }
        else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("goto")) {
                return CommandSignManager.getAllIds();
            }
            else if (isPlayer && (subcommand.equals("addcommand") || subcommand.equals("ac"))) {
                return SignClickType.getNames();
            }
            else if (isPlayer && (subcommand.equals("removecommand") || subcommand.equals("rc"))) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                ArrayList<SignCommand> commands = commandSign.get().getCommands();
                return getRangeStrings(0, commands.size() - 1);
            }
            else if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec"))) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                ArrayList<SignCommand> commands = commandSign.get().getCommands();
                return getRangeStrings(0, commands.size() - 1);
            }
            else if (isPlayer && (subcommand.equals("removerequiredpermission") || subcommand.equals("rrp"))) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                return commandSign.get().getRequiredPermissions().stream().toList();
            }
            else if (isPlayer && (subcommand.equals("removeblockedpermission") || subcommand.equals("rbp"))) {
                Optional<CommandSign> commandSign = CommandSignManager.getLookingAt((Player) sender);
                if (commandSign.isEmpty()) {
                    return null;
                }

                return commandSign.get().getBlockedPermissions().stream().toList();
            }
            else if (isPlayer && (subcommand.equals("setcooldown"))) {
                return Arrays.asList("<cooldownMilliseconds>");
            }
            else if (isPlayer && (subcommand.equals("setmaxclicksperuser"))) {
                return Arrays.asList("<maxClicksPerUser>");
            }
        }
        else if (args.length == 3) {
            String subcommand = args[0].toLowerCase();
            if (isPlayer && (subcommand.equals("addcommand") || subcommand.equals("ac"))) {
                return SignCommandType.getNames();
            }
            else if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec"))) {
                return SignClickType.getNames();
            }
        }
        else if (args.length == 4) {
            String subcommand = args[0].toLowerCase();
            if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec"))) {
                return SignCommandType.getNames();
            }
        }
        else if (args.length == 5) {
            String subcommand = args[0].toLowerCase();
            if (isPlayer && (subcommand.equals("editcommand") || subcommand.equals("ec"))) {
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
        if ((subcommand.equals("reload") || subcommand.equals("rd")) && sender.hasPermission(Permissions.RELOAD_PERMISSION)) {
            SignCommandsPlugin.loadConfigs();
            sender.sendMessage(ChatColor.GOLD + "Configuration reloaded.");
            return true;
        }
        // List Signs
        else if ((subcommand.equals("listsigns") || subcommand.equals("ls")) && sender.hasPermission(Permissions.LIST_SIGNS_PERMISSION)) {
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
                String locationString = StringUtils.getName(location);
                int commandCount = sign.getCommands().size();
                if (commandCount == 1) {
                    sender.sendMessage(ChatColor.GOLD + sign.getId() + " " + locationString + " (" + commandCount + " command)");
                } 
                else {
                    sender.sendMessage(ChatColor.GOLD + sign.getId() + " " + locationString + " (" + commandCount + " commands)");
                }
            }

            return true;
        }
        // Goto
        else if (isPlayer && (subcommand.equals("goto") || subcommand.equals("gt")) && sender.hasPermission(Permissions.GOTO_SIGN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <goto|gt> <signId>");
                return true;
            }

            // Get the command sign
            String id = args[1];
            Optional<CommandSign> commandSign = CommandSignManager.getById(id);
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
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <rename|rn> <newId>");
                return true;
            }

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Check if the new ID is already taken
            String newId = args[1];
            if (CommandSignManager.getById(newId).isPresent()) {
                player.sendMessage(ChatColor.RED + "A sign with that name already exists.");
                return true;
            }

            // Rename the sign
            CommandSignManager.renameSign(commandSign.get(), newId);
            player.sendMessage(ChatColor.GOLD + "Command sign renamed to: " + newId);
            return true;
        }
        // Delete
        else if ((subcommand.equals("delete") || subcommand.equals("dt")) && sender.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
            // Check if the sender provided enough arguments
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /signcommands <delete|dt> <signId>");
                return true;
            }

            // Get the command sign
            String id = args[1];
            Optional<CommandSign> commandSign = CommandSignManager.getById(id);
            if (commandSign.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Sign does not exist.");
                return true;
            }

            // Delete the sign
            CommandSignManager.deleteSign(commandSign.get());
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Check if the block is valid
            MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
            if (mainConfig.ONLY_ALLOW_SIGNS && !isSign(targetBlock.get())) {
                player.sendMessage(ChatColor.RED + "You must be looking at a sign.");
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

            // Add the command to the command sign
            String signCommand = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            CommandSignManager.addCommand(location, signClickType.get(), signCommandType.get(), signCommand);
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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
            if (CommandSignManager.removeCommand(location, index.get())) {
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign at that location
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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
            String newSignCommand = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            if (CommandSignManager.editCommand(location, index.get(), signClickType.get(), signCommandType.get(), newSignCommand)) {
                player.sendMessage(ChatColor.GOLD + "Command edited.");
            } 
            else {
                player.sendMessage(ChatColor.RED + "Failed to edit command.");
            }

            return true;
        }
        // List Commands
        else if (isPlayer && (subcommand.equals("listcommands" ) || subcommand.equals("lc")) && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
            Player player = (Player) sender;

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign at that location
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "No commands assigned to this block.");
                return true;
            }

            // List the commands
            ArrayList<SignCommand> signCommands = commandSign.get().getCommands();
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Sign Commands");
            for (int i = 0; i < signCommands.size(); i++) {
                SignCommand signCommand = signCommands.get(i);
                player.sendMessage(ChatColor.GOLD + "" + i + ". " + ChatColor.WHITE + signCommand.getCommandType().getName() + ": /" + signCommand.getCommand());
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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
        else if (isPlayer && (subcommand.equals("listrequiredpermission") || subcommand.equals("lrp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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
        else if (isPlayer && (subcommand.equals("listblockedpermission") || subcommand.equals("lbp")) && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            Player player = (Player) sender;

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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
        // Set Cooldown
        else if (isPlayer && (subcommand.equals("setcooldown") || subcommand.equals("sc")) && sender.hasPermission(Permissions.SET_COOLDOWN_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setcooldown|sc> <cooldownMilliseconds>");
                return true;
            }

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
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
            commandSign.get().setCooldownMillis(cooldown.get());
            player.sendMessage(ChatColor.GOLD + "Cooldown set to: " + cooldown.get() + " milliseconds.");
            return true;
        }
        // Set Max Clicks
        else if (isPlayer && (subcommand.equals("setmaxclicksperuser") || subcommand.equals("smcpu")) && sender.hasPermission(Permissions.SET_MAX_CLICKS_PERMISSION)) {
            Player player = (Player) sender;

            // Check if the player provided enough arguments
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /signcommands <setmaxclicksperuser|smcpu> <maxClicks>");
                return true;
            }

            // Get the block the player is looking at
            Optional<Block> targetBlock = RayTrace.block(player);
            if (targetBlock.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a block.");
                return true;
            }
            Location location = targetBlock.get().getLocation();

            // Get the command sign
            Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
            if (commandSign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a command sign.");
                return true;
            }

            // Parse the max clicks
            Optional<Integer> maxClicks = StringUtils.parseInteger(args[1]);
            if (maxClicks.isEmpty() || maxClicks.get() < 0) {
                player.sendMessage(ChatColor.RED + "Invalid max clicks value.");
                return true;
            }

            // Set the max clicks
            commandSign.get().setMaxClicksPerUser(maxClicks.get());
            player.sendMessage(ChatColor.GOLD + "Max clicks set to: " + maxClicks.get() + ".");
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
            sender.sendMessage(ChatColor.GOLD + "/signcommands <reload | rd> " + ChatColor.WHITE + "Reload the plugin.");
        }
        if (sender.hasPermission(Permissions.LIST_SIGNS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <listsigns | ls> " + ChatColor.WHITE + "List all command signs.");
        }
        if (isPlayer && sender.hasPermission(Permissions.GOTO_SIGN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <goto | gt> <signId> " + ChatColor.WHITE + "Teleport to a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.RENAME_SIGN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <rename | rn> <newSignId> " + ChatColor.WHITE + "Rename a command sign. This will reset the sign's cooldown and max clicks.");
        }
        if (isPlayer && sender.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <delete | dt> <signId>" + ChatColor.WHITE + "Delete all commands from a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_COMMANDS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <addcommand | ac> <clickType> <commandType> <command> " + ChatColor.WHITE + "Add a command to a command sign.");
             sender.sendMessage(ChatColor.GOLD + "/signcommands <removecommand | rc> <commandIndex> " + ChatColor.WHITE + "Remove a command from a command sign.");
             sender.sendMessage(ChatColor.GOLD + "/signcommands <editcommand | ec> <commandIndex> <clickType> <commandType> <command> " + ChatColor.WHITE + "Edit a command on a command sign.");
             sender.sendMessage(ChatColor.GOLD + "/signcommands <list | lc> " + ChatColor.WHITE + "List a command sign's commands.");
        }
        if (isPlayer && sender.hasPermission(Permissions.MANAGE_PERMISSIONS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <addrequiredpermission | arp> <permission> " + ChatColor.WHITE + "Add a required permission to a command sign. A player must have all required permissions to use the sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <removerequiredpermission | rrp> <permission> " + ChatColor.WHITE + "Remove a required permission from a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <listrequiredpermission | lrp> " + ChatColor.WHITE + "List required permissions of a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <addblockedpermission | abp> <permission> " + ChatColor.WHITE + "Add a blocked permission to a command sign. If a player has any of these permissions they will not be able to use the sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <removeblockedpermission | rbp> <permission> " + ChatColor.WHITE + "Remove a blocked permission from a command sign.");
            sender.sendMessage(ChatColor.GOLD + "/signcommands <listblockedpermission | lbp> " + ChatColor.WHITE + "List blocked permissions of a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.SET_COOLDOWN_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setcooldown | sc> <cooldownMilliseconds> " + ChatColor.WHITE + "Set the cooldown for a command sign.");
        }
        if (isPlayer && sender.hasPermission(Permissions.SET_MAX_CLICKS_PERMISSION)) {
            sender.sendMessage(ChatColor.GOLD + "/signcommands <setmaxclicksperuser | smcpu> <maxClicks> " + ChatColor.WHITE + "Set the max clicks per user for a command sign.");
        }
    }

    private static ArrayList<String> getRangeStrings(int start, int end) {
        ArrayList<String> rangeStrings = new ArrayList<String>();
        for (int i = start; i <= end; i++) {
            rangeStrings.add(String.valueOf(i));
        }
        return rangeStrings;
    }

    private static boolean isSign(@NonNull Block block) {
        BlockState state = block.getState();
        if (state == null) {
            return false;
        }
        return (state instanceof Sign);
    }
}