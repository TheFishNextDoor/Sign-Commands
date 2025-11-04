package fun.sunrisemc.sign_commands.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fun.sunrisemc.sign_commands.permission.Permissions;

public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission(Permissions.COLOR_PERMISSION)) {
            return;
        }

        for (int i = 0; i < event.getLines().length; i++) {
            event.setLine(i, org.bukkit.ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
    }
}