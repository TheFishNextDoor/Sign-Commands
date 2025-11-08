package fun.sunrisemc.sign_commands.permission;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ProtectionCheck {

    private static BlockBreakEvent checking = null;

    public static boolean canBreak(@NonNull Player player, @NonNull Block block) {
        checking = new BlockBreakEvent(block, player);
        Bukkit.getServer().getPluginManager().callEvent(checking);
        boolean cancelled = checking.isCancelled();
        checking = null;
        return !cancelled;
    }

    public static boolean isReal(@NonNull BlockBreakEvent event) {
        return event != checking;
    }
}