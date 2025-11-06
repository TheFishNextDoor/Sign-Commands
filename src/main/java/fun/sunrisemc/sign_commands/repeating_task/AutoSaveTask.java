package fun.sunrisemc.sign_commands.repeating_task;

import org.bukkit.Bukkit;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.command_sign.CommandSignManager;
import fun.sunrisemc.sign_commands.user.CommandSignUserManager;

public class AutoSaveTask {
    private static final int INTERVAL_TICKS = 20 * 60; // 60 Seconds

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