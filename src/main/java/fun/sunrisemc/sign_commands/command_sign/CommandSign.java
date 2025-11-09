package fun.sunrisemc.sign_commands.command_sign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import net.milkbowl.vault.economy.Economy;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.hook.Vault;
import fun.sunrisemc.sign_commands.sign_command.SignClickType;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;
import fun.sunrisemc.sign_commands.user.CommandSignUser;
import fun.sunrisemc.sign_commands.user.CommandSignUserManager;
import fun.sunrisemc.sign_commands.utils.StringUtils;

public class CommandSign {

    // Name

    private @NotNull String name;

    // Location

    private Optional<Location> signLocation = Optional.empty();
    private Optional<String> lastValidSignLocationString = Optional.empty();

    // Commands

    private @NotNull ArrayList<SignCommand> commands = new ArrayList<>();

    // Permissions

    private @NotNull HashSet<String> requiredPermissions = new HashSet<>();
    private @NotNull HashSet<String> blockedPermissions = new HashSet<>();

    // Global Click Tracking

    private long globalClickCooldownMillis = 0;
    private long globalLastClickTimeMillis = 0;

    private int globalMaxClicks = 0;
    private int globalClickLimit = 0;

    // User Click Tracking

    private long userClickCooldownMillis = 0;
    private long lastUserClickCooldownResetTimeMillis = 0;

    private int userMaxClicks = 0;
    private long lastUserClickLimitResetTimeMillis = 0;

    // Click Cost

    private double clickCost = 0.0;

    // Constructors

    protected CommandSign(@NotNull Location location) {
        this.name = generateName();
        this.signLocation = Optional.of(location);
        this.lastUserClickCooldownResetTimeMillis = System.currentTimeMillis();
        this.lastUserClickLimitResetTimeMillis = System.currentTimeMillis();

        CommandSignManager.register(this);
    }

    protected CommandSign(@NotNull YamlConfiguration config, @NotNull String name) {
        this.name = name;

        loadFrom(config);

        CommandSignManager.register(this);
    }

    // Executing

    public boolean attemptExecute(@NotNull Player player, @NotNull SignClickType clickType) {
        if (!hasCommandForClickType(clickType)) {
            return false;
        }

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
        CommandSignUser commandSignUser = CommandSignUserManager.get(player);
        int totalUserClicks = commandSignUser.getSignClickCount(this);
        int maxUserClicks = getUserMaxClicks();
        if (maxUserClicks > 0 && totalUserClicks >= maxUserClicks) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum number of clicks for this sign.");
            return false;
        }

        // Check user and global cooldown
        long remainingGlobalCooldown = getRemainingGlobalCooldown();
        long remainingUserCooldown = getRemainingUserClickCooldown(commandSignUser);
        if (remainingGlobalCooldown > 0 || remainingUserCooldown > 0) {
            if (remainingGlobalCooldown > remainingUserCooldown) {
                player.sendMessage(ChatColor.RED + "This sign is on global cooldown. Please wait " + StringUtils.formatMillis(remainingGlobalCooldown) + " before clicking again.");
                return false;
            }
            else {
                player.sendMessage(ChatColor.RED + "You must wait " + StringUtils.formatMillis(remainingUserCooldown) + " before clicking this sign again.");
                return false;
            }
        }

        // Click Cost
        Optional<Economy> economy = Vault.getEconomy();
        if (economy.isPresent() && clickCost > 0.0) {
            if (!economy.get().has(player, clickCost)) {
                player.sendMessage(ChatColor.RED + "You do not have enough money to click this sign. You need " + economy.get().format(clickCost) + ".");
                return false;
            }

            economy.get().withdrawPlayer(player, clickCost);
            player.sendMessage(ChatColor.GOLD + "You have been charged " + economy.get().format(clickCost) + " for clicking this sign.");
        }

        // Execute command sign
        execute(player, clickType);
        return true;
    }

    public void execute(@NotNull Player player, @NotNull SignClickType clickType) {
        if (!signLocation.isPresent()) {
            return;
        }

        globalLastClickTimeMillis = System.currentTimeMillis();
        globalClickLimit++;

        CommandSignUserManager.get(player).onSignExecute(this);

        for (SignCommand command : commands) {
            command.execute(player, clickType);
        }
    }

    // Name

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String newId) {
        CommandSignManager.unregister(this);
        this.name = newId;
        CommandSignManager.register(this);
    }

    @NotNull
    private String generateName() {
        int idx = 1;
        while (true) {
            String name = "sign-" + idx;
            if (!CommandSignManager.getByName(name).isPresent()) {
                return name;
            }
            idx++;
        }
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

    @NotNull
    public ArrayList<SignCommand> getCommands() {
        return commands;
    }

    public void addCommand(@NotNull SignCommand command) {
        commands.add(command);
    }

    public boolean removeCommand(int index) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }
        commands.remove(index);
        return true;
    }

    public boolean editCommand(int index, @NotNull SignCommand newCommand) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }
        
        commands.set(index, newCommand);
        return true;
    }

    public boolean hasCommandForClickType(@NotNull SignClickType clickType) {
        for (SignCommand command : commands) {
            if (command.getClickType() == SignClickType.ANY_CLICK || command.getClickType() == clickType) {
                return true;
            }
        }
        return false;
    }

    // Required Permissions

    @NotNull
    public HashSet<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    public boolean addRequiredPermission(@NotNull String permission) {
        return requiredPermissions.add(permission);
    }

    public boolean removeRequiredPermission(@NotNull String permission) {
        return requiredPermissions.remove(permission);
    }

    private boolean hasRequiredPermissions(@NotNull Player player) {
        for (String permission : requiredPermissions) {
            if (permission == null) {
                continue;
            }
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }
    
    // Blocked Permissions

    @NotNull
    public HashSet<String> getBlockedPermissions() {
        return blockedPermissions;
    }

    public boolean addBlockedPermission(@NotNull String permission) {
        return blockedPermissions.add(permission);
    }

    public boolean removeBlockedPermission(@NotNull String permission) {
        return blockedPermissions.remove(permission);
    }

    private boolean hasBlockedPermissions(@NotNull Player player) {
        for (String permission : blockedPermissions) {
            if (permission == null) {
                continue;
            }
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // Global Cooldown Millis

    public long getGlobalClickCooldownMillis() {
        return globalClickCooldownMillis;
    }

    public void setGlobalClickCooldownMillis(long cooldownMillis) {
        this.globalClickCooldownMillis = cooldownMillis;
    }

    public void resetGlobalClickCooldown() {
        this.globalLastClickTimeMillis = 0;
    }

    private long getRemainingGlobalCooldown() {
        if (globalClickCooldownMillis <= 0) {
            return 0;
        }
        long elapsedMillis = System.currentTimeMillis() - globalLastClickTimeMillis;
        long remainingMillis = globalClickCooldownMillis - elapsedMillis;
        return Math.max(0, remainingMillis);
    }

    // Global Max Clicks

    public int getGlobalMaxClicks() {
        return globalMaxClicks;
    }

    public void setGlobalMaxClicks(int maxClicks) {
        this.globalMaxClicks = maxClicks;
    }

    public void resetGlobalClickLimit() {
        this.globalClickLimit = 0;
    }

    private boolean checkGlobalMaxClicks() {
        if (globalMaxClicks <= 0) {
            return true;
        }
        return globalClickLimit < globalMaxClicks;
    }

    // User Cooldown Millis

    public long getUserClickCooldownMillis() {
        return userClickCooldownMillis;
    }

    public void setUserClickCooldownMillis(long cooldownMillis) {
        this.userClickCooldownMillis = cooldownMillis;
    }

    public void resetAllUserClickCooldowns() {
        this.lastUserClickCooldownResetTimeMillis = System.currentTimeMillis();
    }

    public long getLastUserClickCooldownResetTimeMillis() {
        return lastUserClickCooldownResetTimeMillis;
    }

    public long getRemainingUserClickCooldown(@NotNull CommandSignUser commandSignUser) {
        long userClickCooldownMillis = getUserClickCooldownMillis();
        if (userClickCooldownMillis <= 0) {
            return 0;
        }

        long lastClickTimeMillis = commandSignUser.getLastSignClickTimeMillis(this);
        if (lastClickTimeMillis < lastUserClickCooldownResetTimeMillis) {
            return 0;
        }

        long elapsedMillis = System.currentTimeMillis() - lastClickTimeMillis;
        return Math.max(0, userClickCooldownMillis - elapsedMillis);
    }

    // User Max Clicks

    public int getUserMaxClicks() {
        return userMaxClicks;
    }

    public void setUserMaxClicks(int maxClicksPerUser) {
        this.userMaxClicks = maxClicksPerUser;
    }

    public void resetAllUserClickLimits() {
        this.lastUserClickLimitResetTimeMillis = System.currentTimeMillis();
    }

    public long getLastUserClickLimitResetTimeMillis() {
        return lastUserClickLimitResetTimeMillis;
    }

    // Click Cost

    public double getClickCost() {
        return clickCost;
    }

    public void setClickCost(double cost) {
        this.clickCost = cost;
    }

    // Loading and Saving

    protected void loadFrom(@NotNull YamlConfiguration config) {
        // Load Location
        if (config.contains(name + ".location")) {
            String locationString = config.getString(name + ".location");

            if (locationString != null) {
                this.lastValidSignLocationString = Optional.of(locationString);
                
                String[] parts = locationString.split(",");
                if (parts.length != 4) {
                    SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + name);
                    return;
                }

                String worldName = parts[0].trim();
                if (worldName == null) {
                    worldName = "";
                }

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
                if (clickTypeString == null) {
                    clickTypeString = "";
                }

                String commandTypeString = entryParts[1].trim();
                if (commandTypeString == null) {
                    commandTypeString = "";
                }

                String commandString = entryParts[2].trim();
                if (commandString == null) {
                    commandString = "";
                }

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

        // Load Global Click Cooldown Millis
        if (config.contains(name + ".global-click-cooldown-millis")) {
            this.globalClickCooldownMillis = config.getLong(name + ".global-click-cooldown-millis");
        }

        // Load Global Last Click Time Millis
        if (config.contains(name + ".global-last-click-time-millis")) {
            this.globalLastClickTimeMillis = config.getLong(name + ".global-last-click-time-millis");
        }

        // Load Global Max Clicks
        if (config.contains(name + ".global-max-clicks")) {
            this.globalMaxClicks = config.getInt(name + ".global-max-clicks");
        }

        // Load Global Total Clicks
        if (config.contains(name + ".global-total-clicks")) {
            this.globalClickLimit = config.getInt(name + ".global-total-clicks");
        }

        // Load User Click Cooldown Millis
        if (config.contains(name + ".user-click-cooldown-millis")) {
            this.userClickCooldownMillis = config.getLong(name + ".user-click-cooldown-millis");
        }

        // Load Last User Click Cooldown Reset Time Millis
        if (config.contains(name + ".last-user-click-cooldown-reset-time-millis")) {
            this.lastUserClickCooldownResetTimeMillis = config.getLong(name + ".last-user-click-cooldown-reset-time-millis");
        }
        else {
            this.lastUserClickCooldownResetTimeMillis = System.currentTimeMillis();
        }

        // Load User Max Clicks
        if (config.contains(name + ".user-max-clicks")) {
            this.userMaxClicks = config.getInt(name + ".user-max-clicks");
        }

        // Load Last User Max Clicks Reset Time Millis
        if (config.contains(name + ".last-user-max-clicks-reset-time-millis")) {
            this.lastUserClickLimitResetTimeMillis = config.getLong(name + ".last-user-max-clicks-reset-time-millis");
        }
        else {
            this.lastUserClickLimitResetTimeMillis = System.currentTimeMillis();
        }

        // Load Click Cost
        if (config.contains(name + ".click-cost")) {
            this.clickCost = config.getDouble(name + ".click-cost");
        }
    }

    protected void saveTo(@NotNull YamlConfiguration config) {
        // Save Location
        String locationString;
        if (signLocation.isPresent()) {
            Location loc = signLocation.get();

            World world = loc.getWorld();
            String worldName = (world != null) ? world.getName() : "Unknown World";
            
            locationString = worldName + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
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

        // Save Global Click Cooldown Millis
        if (globalClickCooldownMillis > 0) {
            config.set(name + ".global-click-cooldown-millis", globalClickCooldownMillis);
        }

        // Save Global Last Click Time Millis
        if (globalLastClickTimeMillis > 0) {
            config.set(name + ".global-last-click-time-millis", globalLastClickTimeMillis);
        }

        // Save Global Max Clicks
        if (globalMaxClicks > 0) {
            config.set(name + ".global-max-clicks", globalMaxClicks);
        }

        // Save Global Total Clicks
        if (globalClickLimit > 0) {
            config.set(name + ".global-total-clicks", globalClickLimit);
        }

        // Save User Click Cooldown Millis
        if (userClickCooldownMillis > 0) {
            config.set(name + ".user-click-cooldown-millis", userClickCooldownMillis);
        }

        // Save Last User Click Cooldown Reset Time Millis
        if (lastUserClickCooldownResetTimeMillis > 0) {
            config.set(name + ".last-user-click-cooldown-reset-time-millis", lastUserClickCooldownResetTimeMillis);
        }

        // Save User Max Clicks
        if (userMaxClicks > 0) {
            config.set(name + ".user-max-clicks", userMaxClicks);
        }

        // Save Last User Max Clicks Reset Time Millis
        if (lastUserClickLimitResetTimeMillis > 0) {
            config.set(name + ".last-user-max-clicks-reset-time-millis", lastUserClickLimitResetTimeMillis);
        }

        // Save Click Cost
        if (clickCost > 0.0) {
            config.set(name + ".click-cost", clickCost);
        }
    }
}