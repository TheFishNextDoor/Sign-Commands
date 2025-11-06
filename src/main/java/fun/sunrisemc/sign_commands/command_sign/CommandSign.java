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

public class CommandSign {

    private String name;

    private Optional<Location> signLocation = Optional.empty();
    private Optional<String> lastValidSignLocationString = Optional.empty();

    private ArrayList<SignCommand> commands = new ArrayList<>();

    private HashSet<String> requiredPermissions = new HashSet<>();
    private HashSet<String> blockedPermissions = new HashSet<>();

    private long cooldownMillis = 0;

    private int maxClicksPerUser = 0;

    CommandSign(@NonNull Location location, @NonNull SignCommand firstSignCommand) {
        this.name = CommandSignManager.generateName();
        this.signLocation = Optional.of(location);
        this.commands.add(firstSignCommand);
    }

    CommandSign(@NonNull YamlConfiguration config, @NonNull String name) {
        this.name = name;

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

        // Load Cooldown
        if (config.contains(name + ".cooldown-millis")) {
            this.cooldownMillis = config.getLong(name + ".cooldown-millis");
        }

        // Load Max Clicks Per User
        if (config.contains(name + ".max-clicks-per-user")) {
            this.maxClicksPerUser = config.getInt(name + ".max-clicks-per-user");
        }
    }

    public String getName() {
        return name;
    }

    public Optional<Location> getSignLocation() {
        return signLocation;
    }

    public void execute(@NonNull Player player, @NonNull SignClickType clickType) {
        if (!signLocation.isPresent()) {
            return;
        }

        for (SignCommand command : commands) {
            command.execute(player, clickType);
        }
    }

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

    public boolean editCommand(int index, @NonNull SignClickType clickType, @NonNull SignCommandType commandType, @NonNull String command) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }

        SignCommand newCommand = new SignCommand(clickType, commandType, command);
        commands.set(index, newCommand);
        return true;
    }

    public HashSet<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    public boolean hasRequiredPermissions(@NonNull Player player) {
        for (String permission : requiredPermissions) {
            if (!player.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean addRequiredPermission(@NonNull String permission) {
        return requiredPermissions.add(permission);
    }

    public boolean removeRequiredPermission(@NonNull String permission) {
        return requiredPermissions.remove(permission);
    }

    public HashSet<String> getBlockedPermissions() {
        return blockedPermissions;
    }

    public boolean hasBlockedPermissions(@NonNull Player player) {
        for (String permission : blockedPermissions) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean addBlockedPermission(@NonNull String permission) {
        return blockedPermissions.add(permission);
    }

    public boolean removeBlockedPermission(@NonNull String permission) {
        return blockedPermissions.remove(permission);
    }

    public long getCooldownMillis() {
        return cooldownMillis;
    }

    public void setCooldownMillis(long cooldownMillis) {
        this.cooldownMillis = cooldownMillis;
    }

    public int getMaxClicksPerUser() {
        return maxClicksPerUser;
    }

    public void setMaxClicksPerUser(int maxClicksPerUser) {
        this.maxClicksPerUser = maxClicksPerUser;
    }

    void setName(@NonNull String newId) {
        this.name = newId;
    }

    void saveTo(@NonNull YamlConfiguration config) {
        if (commands.isEmpty()) {
            return;
        }

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

        // Save Cooldown
        if (cooldownMillis > 0) {
            config.set(name + ".cooldown-millis", cooldownMillis);
        }

        // Save Max Clicks Per User
        if (maxClicksPerUser > 0) {
            config.set(name + ".max-clicks-per-user", maxClicksPerUser);
        }
    }
}