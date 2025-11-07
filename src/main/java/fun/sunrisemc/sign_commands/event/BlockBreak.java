package fun.sunrisemc.sign_commands.event;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.permission.Permissions;
import net.md_5.bungee.api.ChatColor;

public class BlockBreak implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(location);
        if (!commandSign.isPresent()) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission(Permissions.DELETE_SIGN_PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to delete command signs.");
            event.setCancelled(true);
            return;
        }

        if (!player.isSneaking()) {
            player.sendMessage(ChatColor.RED + "You must be sneaking to delete command signs.");
            event.setCancelled(true);
            return;
        }

        commandSign.get().delete();
        player.sendMessage(ChatColor.GOLD + "Command sign deleted.");
    }
}