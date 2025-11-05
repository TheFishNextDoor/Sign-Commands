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
import fun.sunrisemc.sign_commands.user.CommandSignUser;
import fun.sunrisemc.sign_commands.user.CommandSignUserManager;
import fun.sunrisemc.sign_commands.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

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
        Optional<CommandSign> commandSignOptional = CommandSignManager.getAtLocation(event.getClickedBlock().getLocation());
        if (!commandSignOptional.isPresent()) {
            return;
        }
        CommandSign commandSign = commandSignOptional.get();

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

        if (!commandSign.hasRequiredPermissions(player)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to click this sign.");
            return;
        }

        if (commandSign.hasBlockedPermissions(player)) {
            player.sendMessage(ChatColor.RED + "You are blocked from clicking this sign.");
            return;
        }

        
        // Check user cooldown
        CommandSignUser commandSignUser = CommandSignUserManager.get(player);
        String commandSignId = commandSign.getId();
        if (!commandSignUser.checkSignCooldown(commandSignId, commandSign.getCooldownMillis())) {
            Long remainingCooldown = commandSignUser.getRemainingCooldown(commandSignId, commandSign.getCooldownMillis());
            player.sendMessage(ChatColor.RED + "You must wait " + StringUtils.formatMillis(remainingCooldown) + " before clicking this sign again.");
            return;
        }

        // Check max clicks per user
        if (!commandSignUser.checkMaxSignClicks(commandSignId, commandSign.getMaxClicksPerUser())) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of clicks for this sign.");
            return;
        }

        // Execute command sign
        commandSign.execute(player, signClickType.get());
        commandSignUser.onSignClick(commandSignId);
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