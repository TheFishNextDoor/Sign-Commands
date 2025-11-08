package fun.sunrisemc.sign_commands.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.permission.Permissions;
import fun.sunrisemc.sign_commands.permission.ProtectionCheck;
import fun.sunrisemc.sign_commands.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class SignEdit implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        if (args.length == 1) {
            return List.of("setline", "set");
        }
        else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("setline") || subcommand.equals("sl")) {
                return sideNames();
            }
            else if (subcommand.equals("set") || subcommand.equals("s")) {
                return sideNames();
            }
        }
        else if (args.length == 3) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("setline") || subcommand.equals("sl")) {
                Optional<Side> side = parseSide(args[1]);
                if (side.isEmpty()) {
                    return null;
                }
                
                Optional<Sign> sign = rayTraceSign((Player) sender);
                if (sign.isEmpty()) {
                    return null;
                }

                return StringUtils.getRangeStrings(1, lineCount(sign.get(), side.get()));
            }
            else if (subcommand.equals("set") || subcommand.equals("s")) {
                Optional<Side> side = parseSide(args[1]);
                if (side.isEmpty()) {
                    return null;
                }
                
                Optional<Sign> sign = rayTraceSign((Player) sender);
                if (sign.isEmpty()) {
                    return null;
                }

                int maxLines = lineCount(sign.get(), side.get());
                String currentText = String.join(";", sign.get().getSide(side.get()).getLines()).replace('§', '&');
                if (!currentText.isEmpty()) {
                    return List.of(currentText);
                }
                else {
                    String example = "";
                    for (int i = 1; i <= maxLines; i++) {
                        example += "line" + i;
                        if (i < maxLines) {
                            example += ";";
                        }
                    }
                    return List.of("<" + example + ">");
                }

            }
        }
        else if (args.length == 4) {
            String subcommand = args[0].toLowerCase();

            if (subcommand.equals("setline") || subcommand.equals("sl")) {
                Optional<Side> side = parseSide(args[1]);
                if (side.isEmpty()) {
                    return List.of("<text>");
                }

                Optional<Sign> sign = rayTraceSign((Player) sender);
                if (sign.isEmpty()) {
                    return List.of("<text>");
                }

                Optional<Integer> line = StringUtils.parseInteger(args[2]);
                if (line.isEmpty() || line.get() < 1 || line.get() > lineCount(sign.get(), side.get())) {
                    return List.of("<text>");
                }

                String currentText = sign.get().getSide(side.get()).getLine(line.get() - 1).replace('§', '&');
                if (currentText.isEmpty()) {
                    return List.of("<text>");
                } 
                else {
                    return List.of(currentText);
                }
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED  + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            helpMessage(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        // Set Line
        if (subcommand.equals("setline") || subcommand.equals("sl")) {
            // Check if the player provided enough arguments
            if (args.length < 4) {
                player.sendMessage(ChatColor.RED + "Usage: /signedit <setline | sl> <side> <line> <text>");
                return true;
            }

            // Get the sign the player is looking at
            Optional<Sign> sign = rayTraceSign(player);
            if (sign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a sign to use this command.");
                return true;
            }

            // Parse the side
            Optional<Side> side = parseSide(args[1]);
            if (side.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid side. Valid sides are: " + String.join(", ", sideNames()));
                return true;
            }

            // Parse the line number
            Optional<Integer> line = StringUtils.parseInteger(args[2]);
            if (line.isEmpty() || line.get() < 1 || line.get() > lineCount(sign.get(), side.get())) {
                player.sendMessage(ChatColor.RED + "Invalid line number. Valid lines are: " + String.join(", ", StringUtils.getRangeStrings(1, lineCount(sign.get(), side.get()))));
                return true;
            }

            if (!player.hasPermission(Permissions.BYPASS_PROTECTIONS_PERMISSION) && !ProtectionCheck.canBreak(player, sign.get().getBlock())) {
                player.sendMessage(ChatColor.RED + "You do not have permission to edit this sign due to protections.");
                return true;
            }

            // Get the text to set
            String text = String.join(" ", List.of(args).subList(3, args.length));
            if (player.hasPermission(Permissions.SIGN_EDIT_COLOR_PERMISSION)) {
                text = ChatColor.translateAlternateColorCodes('&', text);
            }

            // Set the line text
            sign.get().getSide(side.get()).setLine(line.get() - 1, text);
            sign.get().update();
            player.sendMessage(ChatColor.YELLOW + "Set line " + line.get() + " on the " + StringUtils.normalize(side.get().name()) + " side of the sign to: " + text);
            return true;
        }
        else if (subcommand.equals("set") || subcommand.equals("s")) {
            // Check if the player provided enough arguments
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /signedit <set | s> <side> <line1;line2;line3;line4>");
                return true;
            }

            // Get the sign the player is looking at
            Optional<Sign> sign = rayTraceSign(player);
            if (sign.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You must be looking at a sign to use this command.");
                return true;
            }

            // Parse the side
            Optional<Side> side = parseSide(args[1]);
            if (side.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Invalid side. Valid sides are: " + String.join(", ", sideNames()));
                return true;
            }

            if (!player.hasPermission(Permissions.BYPASS_PROTECTIONS_PERMISSION) && !ProtectionCheck.canBreak(player, sign.get().getBlock())) {
                player.sendMessage(ChatColor.RED + "You do not have permission to edit this sign due to protections.");
                return true;
            }

            // Get the lines to set
            String input = String.join(" ", List.of(args).subList(2, args.length));
            String[] lines = input.split(";");
            int maxLines = lineCount(sign.get(), side.get());
            if (lines.length > maxLines) {
                player.sendMessage(ChatColor.RED + "Too many lines provided. The " + StringUtils.normalize(side.get().name()) + " side of the sign only has " + maxLines + " lines.");
                return true;
            }

            // Set the lines
            for (int i = 0; i < lines.length; i++) {
                if (player.hasPermission(Permissions.SIGN_EDIT_COLOR_PERMISSION)) {
                    lines[i] = ChatColor.translateAlternateColorCodes('&', lines[i]);
                }
                sign.get().getSide(side.get()).setLine(i, lines[i]);
            }
            sign.get().update();
            player.sendMessage(ChatColor.YELLOW + "Set all lines on the " + StringUtils.normalize(side.get().name()) + " side of the sign.");
            return true;
        }

        helpMessage(player);
        return true;
    }

    private void helpMessage(@NonNull Player player) {
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Sign Edit Help");
        player.sendMessage(ChatColor.YELLOW + "/signedit <setline | sl> <side> <line> <text> " + ChatColor.WHITE + "Set a specific line on the sign.");
        player.sendMessage(ChatColor.YELLOW + "/signedit <set | s> <side> <line1;line2;line3;line4> " + ChatColor.WHITE + "Set all lines on the sign at once.");
    }

    private static Optional<Sign> rayTraceSign(@NonNull Player player) {
        RayTraceResult result = player.rayTraceBlocks(64.0);
        if (result == null) {
            return Optional.empty();
        }

        Block block = result.getHitBlock();
        if (block == null) {
            return Optional.empty();
        }

        BlockState state = block.getState();
        if (state == null) {
            return Optional.empty();
        }

        if (!(state instanceof Sign)) {
            return Optional.empty();
        }

        return Optional.of((Sign) state);
    }

    private static ArrayList<String> sideNames() {
        ArrayList<String> sides = new ArrayList<>();
        for (Side side : Side.values()) {
            sides.add(StringUtils.normalize(side.name()));
        }
        return sides;
    }

    private static Optional<Side> parseSide(@NonNull String sideStr) {
        sideStr = StringUtils.normalize(sideStr);
        for (Side side : Side.values()) {
            String sideName = StringUtils.normalize(side.name());
            if (sideName.equals(sideStr)) {
                return Optional.of(side);
            }
        }
        return Optional.empty();
    }

    private static int lineCount(@NonNull Sign sign, @NonNull Side side) {
        return sign.getSide(side).getLines().length;
    }
}