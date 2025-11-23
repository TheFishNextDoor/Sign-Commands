package fun.sunrisemc.signcommands.scheduler;

import org.bukkit.Bukkit;

import fun.sunrisemc.signcommands.SignCommandsPlugin;
import fun.sunrisemc.signcommands.sign.CommandSignManager;
import fun.sunrisemc.signcommands.user.CommandSignUserManager;

public class AutoSaveTask {
    
    private static final int INTERVAL_TICKS = 20 * 60 * 5; // 5 Minutes

    private static int id = -1;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(SignCommandsPlugin.getInstance(), () -> {
            CommandSignManager.saveSigns();
            CommandSignUserManager.saveAll();
        }, INTERVAL_TICKS, INTERVAL_TICKS).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }
}