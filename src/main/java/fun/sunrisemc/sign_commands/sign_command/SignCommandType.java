package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum SignCommandType {

    PLAYER,
    CONSOLE,
    MESSAGE,
    BROADCAST;
    
    public static Optional<SignCommandType> fromName(@NonNull String commandTypeBName) {
        for (SignCommandType commandTypeA : values()) {
            String commandTypeAName = commandTypeA.name();
            if (commandTypeAName == null) {
                return Optional.empty();
            }
            if (normalizeName(commandTypeAName).equals(normalizeName(commandTypeBName))) {
                return Optional.of(commandTypeA);
            }
        }
        return Optional.empty();
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignCommandType commandType : values()) {
            String commandTypeName = commandType.name();
            if (commandTypeName == null) {
                continue;
            }
            names.add(normalizeName(commandTypeName));
        }
        return names;
    }

    private static String normalizeName(@NonNull String name) {
        return name.trim().toLowerCase().replace(" ", "-").replace("_", "-");
    }
}