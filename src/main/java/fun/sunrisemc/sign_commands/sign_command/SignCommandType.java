package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

public enum SignCommandType {

    PLAYER,
    CONSOLE,
    MESSAGE,
    BROADCAST;
    
    public static Optional<SignCommandType> fromString(String typeString) {
        for (SignCommandType type : SignCommandType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignCommandType type : SignCommandType.values()) {
            names.add(type.name().toLowerCase());
        }
        return names;
    }
}