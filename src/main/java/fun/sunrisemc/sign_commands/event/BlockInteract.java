package fun.sunrisemc.sign_commands.event;

import java.util.HashMap;
import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

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
        Action action = event.getAction();
        if (action == null) {
            return;
        }
        Optional<SignClickType> signClickType = SignClickType.fromAction(action);
        if (signClickType.isEmpty()) {
            return;
        }

        // Check if clicked block is a command sign
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Optional<CommandSign> commandSign = CommandSignManager.getByBlock(clickedBlock);
        if (commandSign.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        // Check sign click delay tick
        if (!tickDelayBetwenClicksCheck(player)) {
            return;
        }

        commandSign.get().attemptExecute(player, signClickType.get());
    }

    private boolean tickDelayBetwenClicksCheck(@NonNull Player player) {
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