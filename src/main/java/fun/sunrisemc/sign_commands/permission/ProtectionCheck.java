package fun.sunrisemc.sign_commands.permission;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProtectionCheck {

    private static @Nullable BlockBreakEvent checking = null;

    public static boolean canBreak(@NotNull Player player, @NotNull Block block) {
        BlockBreakEvent eventToCheck = new BlockBreakEvent(block, player);

        checking = eventToCheck;

        Bukkit.getServer().getPluginManager().callEvent(eventToCheck);
        boolean cancelled = eventToCheck.isCancelled();

        checking = null;
        
        return !cancelled;
    }

    public static boolean isReal(@NotNull BlockBreakEvent event) {
        return event != checking;
    }
}