package fun.sunrisemc.signcommands.event;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.permission.Permissions;
import fun.sunrisemc.signcommands.permission.ProtectionCheck;
import fun.sunrisemc.signcommands.sign.CommandSign;
import fun.sunrisemc.signcommands.sign.CommandSignManager;

public class BlockBreak implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        if (!ProtectionCheck.isReal(event)) {
            return;
        }

        Block block = event.getBlock();
        Optional<CommandSign> commandSign = CommandSignManager.getByBlock(block);
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