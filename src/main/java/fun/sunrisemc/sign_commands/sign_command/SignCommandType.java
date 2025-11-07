package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum SignCommandType {

    PLAYER,
    CONSOLE,
    MESSAGE,
    BROADCAST;

    public String getName() {
        return normalizeName(name());
    }
    
    public static Optional<SignCommandType> fromName(@NonNull String name) {
        String commandTypeBName = normalizeName(name);
        for (SignCommandType commandTypeA : values()) {
            if (commandTypeA.getName().equals(commandTypeBName)) {
                return Optional.of(commandTypeA);
            }
        }
        return Optional.empty();
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignCommandType commandType : values()) {
            names.add(commandType.getName());
        }
        return names;
    }

    private static String normalizeName(@NonNull String str) {
        return str.trim().toLowerCase().replace(" ", "-").replace("_", "-");
    }
}