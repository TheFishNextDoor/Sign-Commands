package fun.sunrisemc.sign_commands.utils;

import java.util.Optional;

import org.bukkit.entity.Player;

public class PlayerUtils {

    public static Optional<Player> getPlayerByName(String name) {
        Player player = org.bukkit.Bukkit.getPlayerExact(name);
        return Optional.ofNullable(player);
    }
}