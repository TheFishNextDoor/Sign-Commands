package fun.sunrisemc.sign_commands.hook;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Vault {
    
    private static boolean usingVault = false;

    private static Economy economy = null;
    private static Permission permissions = null;
    private static Chat chat = null;

    public static boolean usingVault() {
        return usingVault;
    }

    public static Economy getEconomy() {
        if (!usingVault) {
            throw new IllegalStateException("Vault not found");
        }
        return economy;
    }

    public static Permission getPermissions() {
        if (!usingVault) {
            throw new IllegalStateException("Vault not found");
        }
        return permissions;
    }

    public static Chat getChat() {
        if (!usingVault) {
            throw new IllegalStateException("Vault not found");
        }
        return chat;
    }

    public static boolean hook(@NonNull JavaPlugin plugin) {
        usingVault = false;
        
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

        usingVault = true;
        return true;
    }
}