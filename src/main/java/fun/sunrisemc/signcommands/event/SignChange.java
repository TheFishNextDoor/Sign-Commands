package fun.sunrisemc.signcommands.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.permission.Permissions;

public class SignChange implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(@NotNull SignChangeEvent event) {
        if (event.getPlayer().hasPermission(Permissions.SIGN_EDIT_COLOR_PERMISSION)) {
            for (int i = 0; i < event.getLines().length; i++) {
                String line = event.getLine(i);
                if (line == null) {
                    continue;
                }
                event.setLine(i, org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }
}