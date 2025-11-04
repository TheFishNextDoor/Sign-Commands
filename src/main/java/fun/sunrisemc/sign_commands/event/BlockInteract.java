package fun.sunrisemc.sign_commands.event;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.sign_command.SignClickType;

public class BlockInteract implements Listener {

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == null) {
            return;
        }

        Optional<SignClickType> signClickType = SignClickType.fromAction(action);
        if (signClickType.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location location = block.getLocation();

        Optional<CommandSign> commandSign = CommandSignManager.get(location);
        if (!commandSign.isPresent()) {
            return;
        }
        
        commandSign.get().execute(player, signClickType.get());
    }
}