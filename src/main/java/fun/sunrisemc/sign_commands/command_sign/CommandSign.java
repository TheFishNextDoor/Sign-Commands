package fun.sunrisemc.sign_commands.command_sign;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.SignCommandsPlugin;
import fun.sunrisemc.sign_commands.sign_command.SignCommand;
import fun.sunrisemc.sign_commands.sign_command.SignCommandType;

public class CommandSign {

    private Optional<Location> signLocation = Optional.empty();

    private ArrayList<SignCommand> commands = new ArrayList<>();

    CommandSign(@NonNull Location location, @NonNull SignCommand firstSignCommand) {
        this.signLocation = Optional.of(location);
        this.commands.add(firstSignCommand);
    }

    CommandSign(YamlConfiguration config, String id) {
        if (config.contains(id + ".location")) {
            String locationString = config.getString(id + ".location");
            
            String[] parts = locationString.split(",");
            if (parts.length != 4) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + id);
                return;
            }

            World world = SignCommandsPlugin.getInstance().getServer().getWorld(parts[0]);
            if (world == null) {
                SignCommandsPlugin.logWarning("World not found for sign configuration: " + id);
                return;
            }

            int x, y, z;
            try {
                x = Integer.parseInt(parts[1]);
                y = Integer.parseInt(parts[2]);
                z = Integer.parseInt(parts[3]);
            }
            catch (NumberFormatException e) {
                SignCommandsPlugin.logSevere("Invalid location for sign configuration: " + id);
                return;
            }

            this.signLocation = Optional.of(new Location(world, x, y, z));
        }

        if (config.contains(id + ".commands")) {
            for (String commandEntry : config.getStringList(id + ".commands")) {
                String[] entryParts = commandEntry.split(":", 2);
                if (entryParts.length != 2) {
                    SignCommandsPlugin.logWarning("Invalid command entry for sign configuration " + id + ": " + commandEntry);
                    continue;
                }

                String commandTypeString = entryParts[0];
                String commaString = entryParts[1];

                Optional<SignCommandType> signCommandType = SignCommandType.fromString(commandTypeString);
                if (signCommandType.isEmpty()) {
                    SignCommandsPlugin.logWarning("Unknown command type for sign configuration " + id + ": " + commandTypeString);
                    continue;
                }

                SignCommand signCommand = new SignCommand(signCommandType.get(), commaString);
                commands.add(signCommand);
            }
        }
    }

    public boolean isValid() {
        return signLocation.isPresent();
    }

    public Optional<Location> getSignLocation() {
        return signLocation;
    }

    public void execute(@NonNull Player player) {
        if (!isValid()) {
            return;
        }

        for (SignCommand command : commands) {
            command.execute(player);
        }
    }

    void addCommand(@NonNull SignCommand command) {
        commands.add(command);
    }
}