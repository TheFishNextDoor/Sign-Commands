package fun.sunrisemc.signcommands.scheduler;

import org.bukkit.Bukkit;

import fun.sunrisemc.signcommands.SignCommandsPlugin;

public class TickCounterTask {

    private static final int INTERVAL_TICKS = 1; // 1 Tick

    private static int id = -1;

    private static long tickCount = 0;

    public static void start() {
        if (id != -1) {
            return;
        }
        id = Bukkit.getScheduler().runTaskTimer(SignCommandsPlugin.getInstance(), () -> {
            if (tickCount == Long.MAX_VALUE) {
                tickCount = 0;
            }
            tickCount++;
        }, INTERVAL_TICKS, INTERVAL_TICKS).getTaskId();
    }

    public static void stop() {
        if (id == -1) {
            return;
        }
        Bukkit.getScheduler().cancelTask(id);
        id = -1;
    }

    public static long getTicksFromServerStart() {
        return tickCount;
    }
}