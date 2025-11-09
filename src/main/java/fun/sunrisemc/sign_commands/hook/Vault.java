package fun.sunrisemc.sign_commands.hook;

import java.util.Optional;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Vault {

    private static @Nullable Economy economy = null;

    private static @Nullable Permission permissions = null;

    private static @Nullable Chat chat = null;

    public static Optional<Economy> getEconomy() {
        return Optional.ofNullable(economy);
    }

    public static Optional<Permission> getPermissions() {
        return Optional.ofNullable(permissions);
    }

    public static Optional<Chat> getChat() {
        return Optional.ofNullable(chat);
    }

    public static boolean hook(@NotNull JavaPlugin plugin) {
        Server server = plugin.getServer();
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        ServicesManager servicesManager = server.getServicesManager();

        RegisteredServiceProvider<@NotNull Economy> economyService = servicesManager.getRegistration(Economy.class);
        if (economyService == null) {
            return false;
        }
        economy = economyService.getProvider();

        RegisteredServiceProvider<@NotNull Permission> permissionService = servicesManager.getRegistration(Permission.class);
        if (permissionService == null) {
            return false;
        }
        permissions = permissionService.getProvider();

        RegisteredServiceProvider<@NotNull Chat> chatService = servicesManager.getRegistration(Chat.class);
        if (chatService == null) {
            return false;
        }
        chat = chatService.getProvider();

        return true;
    }
}