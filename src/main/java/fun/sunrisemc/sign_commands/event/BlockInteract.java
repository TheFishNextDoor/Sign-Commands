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
        // Check correct action
        Optional<SignClickType> signClickType = SignClickType.fromAction(event.getAction());
        if (signClickType.isEmpty()) {
            return;
        }

        // Check if clicked block is a command sign
        Optional<CommandSign> commandSign = CommandSignManager.getAtLocation(event.getClickedBlock().getLocation());
        if (!commandSign.isPresent()) {
            return;
        }

         // Don't run sign commands while sneaking
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        // Check permission
        if (!player.hasPermission("signcommands.use")) {
            return;
        }
        
        // Prevent editing the sign
        event.setCancelled(true);

        // Check cooldown
        long currentTick = TickCounterTask.getTicksFromServerStart();  
        if (isOnCooldown(player, currentTick)) {
            return;
        }
        setLastInteractionTick(player, currentTick);
        
        // Execute command sign
        commandSign.get().execute(player, signClickType.get());
    }

    private boolean isOnCooldown(Player player, long currentTicks) {
        String key = toKey(player);
        Long lastInteractionTick = lastInteractionTickMap.get(key);
        if (lastInteractionTick == null) {
            return false;
        }
        MainConfig mainConfig = SignCommandsPlugin.getMainConfig();
        return (currentTicks - lastInteractionTick) < mainConfig.SIGN_CLICK_COOLDOWN_TICKS;
    }

    private void setLastInteractionTick(Player player, long currentTicks) {
        String key = toKey(player);
        lastInteractionTickMap.put(key, currentTicks);
    }

    private String toKey(Player player) {
        return player.getUniqueId().toString();
    }
}