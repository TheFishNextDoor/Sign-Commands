package fun.sunrisemc.sign_commands.user;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;

public class CommandSignUserManager {

    private static ConcurrentHashMap<UUID, CommandSignUser> signUsers = new ConcurrentHashMap<>();

    public static CommandSignUser get(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        CommandSignUser signUser = signUsers.get(uuid);
        if (signUser == null) {
            signUser = new CommandSignUser(uuid);
            signUsers.put(uuid, signUser);
        }
        return signUser;
    }

    public static void preload(@NotNull Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(SignCommandsPlugin.getInstance(), () -> get(player));
    }

    public static boolean unload(@NotNull UUID uuid) {
        CommandSignUser signUser = signUsers.remove(uuid);
        return signUser != null;
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

    public static void saveAll() {
        signUsers.values().forEach(CommandSignUser::save);
    }
}