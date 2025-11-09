package fun.sunrisemc.sign_commands.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.sign_commands.permission.Permissions;

public class SignChange implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(@NotNull SignChangeEvent event) {
        if (!event.getPlayer().hasPermission(Permissions.SIGN_EDIT_COLOR_PERMISSION)) {
            return;
        }

        for (int i = 0; i < event.getLines().length; i++) {
            event.setLine(i, org.bukkit.ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
    }
}