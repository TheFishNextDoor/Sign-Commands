package fun.sunrisemc.signcommands.sign.command;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.utils.StringUtils;

public enum SignCommandType {

    CONSOLE,
    PLAYER,
    OP,
    MESSAGE,
    BROADCAST;

    @NotNull
    public String getName() {
        return StringUtils.normalize(name());
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

    @NotNull
    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignCommandType commandType : values()) {
            names.add(commandType.getName());
        }
        return names;
    }
}