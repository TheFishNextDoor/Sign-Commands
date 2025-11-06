package fun.sunrisemc.sign_commands.command_sign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.sign_command.SignClickType;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;
import fun.sunrisemc.sign_commands.user.CommandSignUser;
import fun.sunrisemc.sign_commands.user.CommandSignUserManager;
import fun.sunrisemc.sign_commands.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class CommandSign {

    private String name;

    private Optional<Location> signLocation = Optional.empty();
    private Optional<String> lastValidSignLocationString = Optional.empty();

    private ArrayList<SignCommand> commands = new ArrayList<>();

    private HashSet<String> requiredPermissions = new HashSet<>();
    private HashSet<String> blockedPermissions = new HashSet<>();

    private long lastClickTimeMillis = 0;

    private long userClickCooldownMillis = 0;

    private long globalClickCooldownMillis = 0;

    private int totalClicks = 0;

    private int userMaxClicks = 0;

    private int globalMaxClicks = 0;

    public CommandSign(@NonNull Location location) {
        this.name = CommandSignManager.generateName();
        this.signLocation = Optional.of(location);

        CommandSignManager.register(this);
    }

    protected CommandSign(@NonNull YamlConfiguration config, @NonNull String name) {
        this.name = name;

        loadFrom(config);

        CommandSignManager.register(this);
    }

    // Executing

    public boolean attemptExecute(@NonNull Player player, @NonNull SignClickType clickType) {
        if (!player.hasPermission("signcommands.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use command signs.");
            return false;
        }

        // Check command sign permissions
        if (!hasRequiredPermissions(player)) {
            player.sendMessage(ChatColor.RED + "You do not have permission to click this sign.");
            return false;
        }

        // Check blocked permissions
        if (hasBlockedPermissions(player)) {
            player.sendMessage(ChatColor.RED + "You are blocked from clicking this sign.");
            return false;
        }

        // Check global max clicks
        if (!checkGlobalMaxClicks()) {
            player.sendMessage(ChatColor.RED + "This sign has reached its global maximum number of clicks.");
            return false;
        }

        // Check user max clicks
        String name = getName();
        CommandSignUser commandSignUser = CommandSignUserManager.get(player);
        if (!commandSignUser.checkMaxSignClicks(name, getUserMaxClicks())) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of clicks for this sign.");
            return false;
        }

        // Check global cooldown
        long remainingGlobalCooldown = getRemainingCooldown();
        if (remainingGlobalCooldown > 0) {
            player.sendMessage(ChatColor.RED + "This sign is on global cooldown. Please wait " + StringUtils.formatMillis(remainingGlobalCooldown) + " before clicking again.");
            return false;
        }
        
        // Check user cooldown
        if (!commandSignUser.checkSignCooldown(name, getUserClickCooldownMillis())) {
            Long remainingCooldown = commandSignUser.getRemainingCooldown(name, getUserClickCooldownMillis());
            player.sendMessage(ChatColor.RED + "You must wait " + StringUtils.formatMillis(remainingCooldown) + " before clicking this sign again.");
            return false;
        }

        // Execute command sign
        execute(player, clickType);
        commandSignUser.onSignClick(name);
        return true;
    }

    public void execute(@NonNull Player player, @NonNull SignClickType clickType) {
        if (!signLocation.isPresent()) {
            return;
        }

        lastClickTimeMillis = System.currentTimeMillis();
        totalClicks++;

        for (SignCommand command : commands) {
            command.execute(player, clickType);
        }
    }

    // Name

    public String getName() {
        return name;
    }

    public void setName(@NonNull String newId) {
        CommandSignManager.unregister(this);
        this.name = newId;
        CommandSignManager.register(this);
    }

    // Delete

    public void delete() {
        CommandSignManager.unregister(this);
    }

    // Location

    public Optional<Location> getSignLocation() {
        return signLocation;
    }

    // Commands

    public ArrayList<SignCommand> getCommands() {
        return commands;
    }

    public void addCommand(@NonNull SignCommand command) {
        commands.add(command);
    }

    public boolean removeCommand(int index) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }
        commands.remove(index);
        return true;
    }

    public boolean editCommand(int index, @NonNull SignCommand newCommand) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }
        
        commands.set(index, newCommand);
        return true;
    }

    // Required Permissions

    public HashSet<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    public boolean addRequiredPermission(@NonNull String permission) {
        return requiredPermissions.add(permission);
    }

    public boolean removeRequiredPermission(@NonNull String permission) {
        return requiredPermissions.remove(permission);
    }

    private boolean hasRequiredPermissions(@NonNull Player player) {
        for (String permission : requiredPermissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
    
    // Blocked Permissions

    public HashSet<String> getBlockedPermissions() {
        return blockedPermissions;
    }

    public boolean addBlockedPermission(@NonNull String permission) {
        return blockedPermissions.add(permission);
    }

    public boolean removeBlockedPermission(@NonNull String permission) {
        return blockedPermissions.remove(permission);
    }

    private boolean hasBlockedPermissions(@NonNull Player player) {
        for (String permission : blockedPermissions) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // Cooldown Millis

    public long getUserClickCooldownMillis() {
        return userClickCooldownMillis;
    }

    public void setUserClickCooldownMillis(long cooldownMillis) {
        this.userClickCooldownMillis = cooldownMillis;
    }

    // Global Cooldown Millis

    public void setGlobalClickCooldownMillis(long cooldownMillis) {
        this.globalClickCooldownMillis = cooldownMillis;
    }

    private long getRemainingCooldown() {
        if (globalClickCooldownMillis <= 0) {
            return 0;
        }
        long elapsedMillis = System.currentTimeMillis() - lastClickTimeMillis;
        long remainingMillis = globalClickCooldownMillis - elapsedMillis;
        return Math.max(0, remainingMillis);
    }

    // User Max Clicks

    public int getUserMaxClicks() {
        return userMaxClicks;
    }

    public void setUserMaxClicks(int maxClicksPerUser) {
        this.userMaxClicks = maxClicksPerUser;
    }

    // Global Max Clicks

    public void setGlobalMaxClicks(int maxClicks) {
        this.globalMaxClicks = maxClicks;
    }

    private boolean checkGlobalMaxClicks() {
        if (globalMaxClicks <= 0) {
            return true;
        }
        return totalClicks < globalMaxClicks;
    }

    // Loading and Saving

    protected void loadFrom(@NonNull YamlConfiguration config) {
        // Load Location
        if (config.contains(name + ".location")) {
            String locationString = config.getString(name + ".location");

            this.lastValidSignLocationString = Optional.of(locationString);
            
            String[] parts = locationString.split(",");
            if (parts.length != 4) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + name);
                return;
            }

            String worldName = parts[0].trim();
            String xString = parts[1].trim();
            String yString = parts[2].trim();
            String zString = parts[3].trim();

            World world = SignCommandsPlugin.getInstance().getServer().getWorld(worldName);
            if (world == null) {
                SignCommandsPlugin.logWarning("World not found for sign configuration: " + name);
                return;
            }

            int x, y, z;
            try {
                x = Integer.parseInt(xString);
                y = Integer.parseInt(yString);
                z = Integer.parseInt(zString);
            }
            catch (NumberFormatException e) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + name);
                return;
            }

            this.signLocation = Optional.of(new Location(world, x, y, z));
        }

        // Load Commands
        if (config.contains(name + ".commands")) {
            for (String commandEntry : config.getStringList(name + ".commands")) {
                String[] entryParts = commandEntry.split(":", 3);
                if (entryParts.length != 3) {
                    SignCommandsPlugin.logWarning("Invalid command entry for sign configuration " + name + ": " + commandEntry);
                    continue;
                }

                String clickTypeString = entryParts[0].trim();
                String commandTypeString = entryParts[1].trim();
                String commandString = entryParts[2].trim();

                Optional<SignClickType> signClickType = SignClickType.fromName(clickTypeString);
                if (signClickType.isEmpty()) {
                    SignCommandsPlugin.logWarning("Unknown click type for sign configuration " + name + ": " + clickTypeString);
                    continue;
                }

                Optional<SignCommandType> signCommandType = SignCommandType.fromName(commandTypeString);
                if (signCommandType.isEmpty()) {
                    SignCommandsPlugin.logWarning("Unknown command type for sign configuration " + name + ": " + commandTypeString);
                    continue;
                }

                SignCommand signCommand = new SignCommand(signClickType.get(), signCommandType.get(), commandString);
                commands.add(signCommand);
            }
        }

        // Load Required Permissions
        for (String permission : config.getStringList(name + ".required-permissions")) {
            requiredPermissions.add(permission);
        }

        // Load Blocked Permissions
        for (String permission : config.getStringList(name + ".blocked-permissions")) {
            blockedPermissions.add(permission);
        }

        // Load User Cooldown
        if (config.contains(name + ".user-click-cooldown-millis")) {
            this.userClickCooldownMillis = config.getLong(name + ".user-click-cooldown-millis");
        }

        // Load Max Clicks Per User
        if (config.contains(name + ".user-max-clicks")) {
            this.userMaxClicks = config.getInt(name + ".user-max-clicks");
        }
    }

    protected void saveTo(@NonNull YamlConfiguration config) {
        // Save Location
        String locationString;
        if (signLocation.isPresent()) {
            Location loc = signLocation.get();
            locationString = loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
        }
        else if (lastValidSignLocationString.isPresent()) {
            locationString = lastValidSignLocationString.get();
        }
        else {
            return;
        }
        config.set(name + ".location", locationString);

        // Save Commands
        ArrayList<String> commandEntries = new ArrayList<>();
        for (SignCommand command : commands) {
            String entry = command.getClickType().getName() + " : " + command.getCommandType().getName() + " : " + command.getCommand();
            commandEntries.add(entry);
        }
        config.set(name + ".commands", commandEntries);

        // Save Required Permissions
        if (!requiredPermissions.isEmpty()) {
            config.set(name + ".required-permissions", new ArrayList<>(requiredPermissions));
        }
        

        // Save Blocked Permissions
        if (!blockedPermissions.isEmpty()) {
            config.set(name + ".blocked-permissions", new ArrayList<>(blockedPermissions));
        }

        // Save User Click Cooldown Millis
        if (userClickCooldownMillis > 0) {
            config.set(name + ".user-click-cooldown-millis", userClickCooldownMillis);
        }

        // Save User Max Clicks
        if (userMaxClicks > 0) {
            config.set(name + ".user-max-clicks", userMaxClicks);
        }
    }
}