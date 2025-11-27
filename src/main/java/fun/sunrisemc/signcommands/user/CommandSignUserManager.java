package fun.sunrisemc.signcommands.user;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.SignCommandsPlugin;

public class CommandSignUserManager {

    private static @NotNull ConcurrentHashMap<UUID, CommandSignUser> signUsers = new ConcurrentHashMap<>();

    // Getting

    @NotNull
    public static CommandSignUser get(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        CommandSignUser signUser = signUsers.get(uuid);
        if (signUser == null) {
            signUser = new CommandSignUser(uuid);
            signUsers.put(uuid, signUser);
        }
        return signUser;
    }

    // Loading

    public static void preload(@NotNull Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(SignCommandsPlugin.getInstance(), () -> get(player));
    }

    public static void loadOnline() {
        signUsers.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == null) {
                continue;
            }
            get(player);
        }
    }

    // Saving and Unloading

    public static void saveAll() {
        signUsers.values().forEach(CommandSignUser::save);
    }

    protected static boolean unload(@NotNull UUID uuid) {
        CommandSignUser signUser = signUsers.remove(uuid);
        return signUser != null;
    }
}