package fun.sunrisemc.sign_commands.sign_command;

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
}