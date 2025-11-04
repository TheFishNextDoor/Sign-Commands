package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

import fun.sunrisemc.sign_commands.utils.StringUtils;

public enum SignCommandType {

    PLAYER,
    CONSOLE,
    MESSAGE,
    BROADCAST;

    public String getName() {
        return StringUtils.normalizeString(name());
    }
    
    public static Optional<SignCommandType> fromName(@NonNull String name) {
        String commandTypeBName = StringUtils.normalizeString(name);
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
}