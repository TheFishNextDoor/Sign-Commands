package fun.sunrisemc.sign_commands.event;

import java.util.HashMap;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.command_sign.CommandSign;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.config.MainConfig;
import fun.sunrisemc.sign_commands.repeating_task.TickCounterTask;
import fun.sunrisemc.sign_commands.sign_command.SignClickType;

public class BlockInteract implements Listener {

    private HashMap<String, Long> lastInteractionTickMap = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onBlockInteract(PlayerInteractEvent event) {
        // Don't execute command signs while sneaking
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        // Check correct action
        Optional<SignClickType> signClickType = SignClickType.fromAction(event.getAction());
        if (signClickType.isEmpty()) {
            return;
        }

        // Check if clicked block is a command sign
        Optional<CommandSign> commandSignOptional = CommandSignManager.getAtLocation(event.getClickedBlock().getLocation());
        if (!commandSignOptional.isPresent()) {
            return;
        }
        CommandSign commandSign = commandSignOptional.get();

        // Prevent editing command signs
        event.setCancelled(true);

        // Check sign click delay tick
        if (tickDelayBetwenClicksCheck(player)) {
            commandSign.attemptExecute(player, signClickType.get());
        }
    }

    private boolean tickDelayBetwenClicksCheck (Player player) {
        MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
        long currentTicks = TickCounterTask.getTicksFromServerStart();
        String key = player.getUniqueId().toString();
        long lastInteractionTick = lastInteractionTickMap.getOrDefault(key, 0L);
        if ((currentTicks - lastInteractionTick) < mainConfig.SIGN_CLICK_COOLDOWN_TICKS) {
            lastInteractionTickMap.put(key, currentTicks);
            return true;
        }
        return false;
    }
}