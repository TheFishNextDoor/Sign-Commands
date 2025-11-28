package fun.sunrisemc.signcommands.sign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.SignCommandsPlugin;
import fun.sunrisemc.signcommands.hook.Vault;
import fun.sunrisemc.signcommands.sign.command.SignClickType;
import fun.sunrisemc.signcommands.sign.command.SignCommand;
import fun.sunrisemc.signcommands.sign.command.SignCommandType;
import fun.sunrisemc.signcommands.user.CommandSignUser;
import fun.sunrisemc.signcommands.user.CommandSignUserManager;
import fun.sunrisemc.signcommands.utils.StringUtils;
import fun.sunrisemc.signcommands.utils.YAMLUtils;
import net.milkbowl.vault.economy.Economy;

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

    private int globalClickLimit = 0;
    private int globalClickCount = 0;

    // User Click Tracking

    private long userClickCooldownMillis = 0;
    private long lastUserClickCooldownResetTimeMillis = 0;

    private int userClickLimit = 0;
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
        // Name
        
        this.name = name;

        // Location

        Optional<String> locationString = YAMLUtils.getString(config, name + ".location");
        if (locationString.isPresent()) {
            this.lastValidSignLocationString = locationString;
                
            String[] parts = locationString.get().split(",");
            if (parts.length != 4) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + name);
                return;
            }

            String worldName = parts[0].trim();

            World world = SignCommandsPlugin.getInstance().getServer().getWorld(worldName);
            if (world == null) {
                SignCommandsPlugin.logWarning("World not found for sign configuration: " + name);
                return;
            }

            Optional<Integer> x = StringUtils.parseInteger(parts[1]);
            Optional<Integer> y = StringUtils.parseInteger(parts[2]);
            Optional<Integer> z = StringUtils.parseInteger(parts[3]);
            if (x.isEmpty() || y.isEmpty() || z.isEmpty()) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + name);
                return;
            }

            this.signLocation = Optional.of(new Location(world, x.get(), y.get(), z.get()));
        }

        // Commands

        for (String commandEntry : config.getStringList(name + ".commands")) {
            String[] entryParts = commandEntry.split(":", 3);
            if (entryParts.length != 3) {
                SignCommandsPlugin.logWarning("Invalid command entry for sign configuration " + name + ": " + commandEntry);
                continue;
            }

            String clickTypeString = entryParts[0].trim();
            Optional<SignClickType> signClickType = SignClickType.fromName(clickTypeString);
            if (signClickType.isEmpty()) {
                SignCommandsPlugin.logWarning("Unknown click type for sign configuration " + name + ": " + clickTypeString);
                continue;
            }

            String commandTypeString = entryParts[1].trim();
            Optional<SignCommandType> signCommandType = SignCommandType.fromName(commandTypeString);
            if (signCommandType.isEmpty()) {
                SignCommandsPlugin.logWarning("Unknown command type for sign configuration " + name + ": " + commandTypeString);
                continue;
            }

            SignCommand signCommand = new SignCommand(signClickType.get(), signCommandType.get(), entryParts[2].trim());
            commands.add(signCommand);
        }

        // Permissions

        for (String permission : config.getStringList(name + ".required-permissions")) {
            this.requiredPermissions.add(permission);
        }

        for (String permission : config.getStringList(name + ".blocked-permissions")) {
            this.blockedPermissions.add(permission);
        }

        // Global Click Tracking

        this.globalClickCooldownMillis = YAMLUtils.getLong(config, name + ".global-click-cooldown-millis").orElse(0L);

        this.globalLastClickTimeMillis = YAMLUtils.getLong(config, name + ".global-last-click-time-millis").orElse(0L);

        this.globalClickLimit = YAMLUtils.getInt(config, name + ".global-click-limit").orElse(0);

        this.globalClickCount = YAMLUtils.getInt(config, name + ".global-click-count").orElse(0);

        // User Click Tracking

        long currentTimeMillis = System.currentTimeMillis();

        this.userClickCooldownMillis = YAMLUtils.getLong(config, name + ".user-click-cooldown-millis").orElse(0L);

        this.lastUserClickCooldownResetTimeMillis = YAMLUtils.getLong(config, name + ".last-user-click-cooldown-reset-time-millis").orElse(currentTimeMillis);

        this.userClickLimit = YAMLUtils.getInt(config, name + ".user-click-limit").orElse(0);

        this.lastUserClickLimitResetTimeMillis = YAMLUtils.getLong(config, name + ".last-user-click-limit-reset-time-millis").orElse(currentTimeMillis);

        // Click Cost

        this.clickCost = YAMLUtils.getDouble(config, name + ".click-cost").orElse(0.0);

        // Register Sign

        CommandSignManager.register(this);
    }

    // Executing

    public boolean attemptExecute(@NotNull Player player, @NotNull SignClickType clickType) {
        // Check if sign has command for click type
        if (!hasCommandForClickType(clickType)) {
            return false;
        }

        // Check general permission
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
        if (!checkGlobalClickLimit()) {
            player.sendMessage(ChatColor.RED + "This sign has reached its global click limit.");
            return false;
        }

        // Check user max clicks
        CommandSignUser commandSignUser = CommandSignUserManager.get(player);
        int totalUserClicks = commandSignUser.getSignClickCount(this);
        int maxUserClicks = getUserClickLimit();
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
        globalClickCount++;

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

    public int getGlobalClickLimit() {
        return globalClickLimit;
    }

    public void setGlobalClickLimit(int globalClickLimit) {
        this.globalClickLimit = globalClickLimit;
    }

    public void resetGlobalClickLimit() {
        this.globalClickCount = 0;
    }

    private boolean checkGlobalClickLimit() {
        if (globalClickLimit <= 0) {
            return true;
        }
        return globalClickCount < globalClickLimit;
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

    public int getUserClickLimit() {
        return userClickLimit;
    }

    public void setUserClickLimit(int userClickLimit) {
        this.userClickLimit = userClickLimit;
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

    // Saving

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

        // Save Global Click Limit
        if (globalClickLimit > 0) {
            config.set(name + ".global-click-limit", globalClickLimit);
        }

        // Save Global Total Clicks
        if (globalClickCount > 0) {
            config.set(name + ".global-click-count", globalClickCount);
        }

        // Save User Click Cooldown Millis
        if (userClickCooldownMillis > 0) {
            config.set(name + ".user-click-cooldown-millis", userClickCooldownMillis);
        }

        // Save Last User Click Cooldown Reset Time Millis
        if (lastUserClickCooldownResetTimeMillis > 0) {
            config.set(name + ".last-user-click-cooldown-reset-time-millis", lastUserClickCooldownResetTimeMillis);
        }

        // Save User Click Limit
        if (userClickLimit > 0) {
            config.set(name + ".user-click-limit", userClickLimit);
        }

        // Save Last User Click Limit Reset Time Millis
        if (lastUserClickLimitResetTimeMillis > 0) {
            config.set(name + ".last-user-click-limit-reset-time-millis", lastUserClickLimitResetTimeMillis);
        }

        // Save Click Cost
        if (clickCost > 0.0) {
            config.set(name + ".click-cost", clickCost);
        }
    }
}