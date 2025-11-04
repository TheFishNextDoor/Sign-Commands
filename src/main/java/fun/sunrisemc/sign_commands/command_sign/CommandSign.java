package fun.sunrisemc.sign_commands.command_sign;

import java.util.ArrayList;
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

    private String id;

    private Optional<Location> signLocation = Optional.empty();
    private Optional<String> lastValidSignLocationString = Optional.empty();

    private ArrayList<SignCommand> commands = new ArrayList<>();

    CommandSign(@NonNull Location location, @NonNull SignCommand firstSignCommand) {
        this.id = CommandSignManager.generateId();
        this.signLocation = Optional.of(location);
        this.commands.add(firstSignCommand);
    }

    CommandSign(YamlConfiguration config, String id) {
        this.id = id;

        // Load Location
        if (config.contains(id + ".location")) {
            String locationString = config.getString(id + ".location");

            this.lastValidSignLocationString = Optional.of(locationString);
            
            String[] parts = locationString.split(",");
            if (parts.length != 4) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + id);
                return;
            }

            String worldName = parts[0].trim();
            String xString = parts[1].trim();
            String yString = parts[2].trim();
            String zString = parts[3].trim();

            World world = SignCommandsPlugin.getInstance().getServer().getWorld(worldName);
            if (world == null) {
                SignCommandsPlugin.logWarning("World not found for sign configuration: " + id);
                return;
            }

            int x, y, z;
            try {
                x = Integer.parseInt(xString);
                y = Integer.parseInt(yString);
                z = Integer.parseInt(zString);
            }
            catch (NumberFormatException e) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + id);
                return;
            }

            this.signLocation = Optional.of(new Location(world, x, y, z));
        }

        // Load Commands
        if (config.contains(id + ".commands")) {
            for (String commandEntry : config.getStringList(id + ".commands")) {
                String[] entryParts = commandEntry.split(":", 3);
                if (entryParts.length != 3) {
                    SignCommandsPlugin.logWarning("Invalid command entry for sign configuration " + id + ": " + commandEntry);
                    continue;
                }

                String clickTypeString = entryParts[0].trim();
                String commandTypeString = entryParts[1].trim();
                String commandString = entryParts[2].trim();

                Optional<SignClickType> signClickType = SignClickType.fromName(clickTypeString);
                if (signClickType.isEmpty()) {
                    SignCommandsPlugin.logWarning("Unknown click type for sign configuration " + id + ": " + clickTypeString);
                    continue;
                }

                Optional<SignCommandType> signCommandType = SignCommandType.fromName(commandTypeString);
                if (signCommandType.isEmpty()) {
                    SignCommandsPlugin.logWarning("Unknown command type for sign configuration " + id + ": " + commandTypeString);
                    continue;
                }

                SignCommand signCommand = new SignCommand(signClickType.get(), signCommandType.get(), commandString);
                commands.add(signCommand);
            }
        }
    }

    public String getId() {
        return id;
    }

    public boolean isValid() {
        return signLocation.isPresent();
    }

    public Optional<Location> getSignLocation() {
        return signLocation;
    }

    public ArrayList<SignCommand> getCommands() {
        return commands;
    }

    public void execute(@NonNull Player player, @NonNull SignClickType clickType) {
        if (!isValid()) {
            return;
        }

        for (SignCommand command : commands) {
            command.execute(player, clickType);
        }
    }

    void setId(@NonNull String newId) {
        this.id = newId;
    }

    void addCommand(@NonNull SignCommand command) {
        commands.add(command);
    }

    boolean removeCommand(int index) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }
        commands.remove(index);
        return true;
    }

    boolean editCommand(int index, @NonNull SignClickType clickType, @NonNull SignCommandType commandType, @NonNull String command) {
        if (index < 0 || index >= commands.size()) {
            return false;
        }

        SignCommand newCommand = new SignCommand(clickType, commandType, command);
        commands.set(index, newCommand);
        return true;
    }

    void save(@NonNull YamlConfiguration config) {
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
            locationString = "unknown, 0, 0, 0";
        }
        config.set(id + ".location", locationString);

        // Save Commands
        ArrayList<String> commandEntries = new ArrayList<>();
        for (SignCommand command : commands) {
            String entry = command.getClickType().getName() + " : " + command.getCommandType().getName() + " : " + command.getCommand();
            commandEntries.add(entry);
        }
        config.set(id + ".commands", commandEntries);
    }
}