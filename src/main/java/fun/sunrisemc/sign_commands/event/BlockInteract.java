package fun.sunrisemc.sign_commands.event;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;

public class BlockInteract implements Listener {

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (player == null || block == null) {
            return;
        }
        Location location = block.getLocation();
        Optional<CommandSign> commandSign = CommandSignManager.get(location);
        if (!commandSign.isPresent()) {
            return;
        }
        commandSign.get().execute(player);
    }
}