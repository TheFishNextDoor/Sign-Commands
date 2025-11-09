package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.sign_commands.utils.StringUtils;

public enum SignCommandType {

    PLAYER,
    CONSOLE,
    MESSAGE,
    BROADCAST;

    public String getName() {
        String name = name();
        if (name == null) {
            return "";
        }
        return StringUtils.normalize(name);
    }
    
    public static Optional<SignCommandType> fromName(@NotNull String name) {
        String commandTypeBName = StringUtils.normalize(name);
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