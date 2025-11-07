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

        // Check valid action
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

        event.setCancelled(true);

        // Check sign click delay tick
        if (!tickDelayBetwenClicksCheck(player)) {
            return;
        }

        commandSign.attemptExecute(player, signClickType.get());
    }

    private boolean tickDelayBetwenClicksCheck(Player player) {
        MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
        String key = player.getUniqueId().toString();
        long currentTicks = TickCounterTask.getTicksFromServerStart();
        long lastInteractionTick = lastInteractionTickMap.getOrDefault(key, 0L);
        long ticksSinceLastInteraction = currentTicks - lastInteractionTick;
        if (ticksSinceLastInteraction >= mainConfig.SIGN_CLICK_DELAY_TICKS) {
            lastInteractionTickMap.put(key, currentTicks);
            return true;
        }
        return false;
    }
}