package fun.sunrisemc.sign_commands.hook;

import java.util.Optional;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Vault {

    private static Economy economy = null;

    private static Permission permissions = null;

    private static Chat chat = null;

    public static Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    public static Optional<Permission> getPermissions() {
        return Optional.ofNullable(permissions);
    }

    public static Optional<Chat> getChat() {
        return Optional.ofNullable(chat);
    }

    public static boolean hook(@NonNull JavaPlugin plugin) {
        Server server = plugin.getServer();
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> economyService = server.getServicesManager().getRegistration(Economy.class);
        if (economyService == null) {
            return false;
        }
        economy = economyService.getProvider();

        RegisteredServiceProvider<Permission> permissionService = server.getServicesManager().getRegistration(Permission.class);
        if (permissionService == null) {
            return false;
        }
        permissions = permissionService.getProvider();

        RegisteredServiceProvider<Chat> chatService = server.getServicesManager().getRegistration(Chat.class);
        if (chatService == null) {
            return false;
        }
        chat = chatService.getProvider();

        return true;
    }
}