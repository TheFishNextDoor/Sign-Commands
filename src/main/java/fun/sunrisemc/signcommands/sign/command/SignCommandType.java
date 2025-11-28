package fun.sunrisemc.signcommands.sign.command;

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
        return StringUtils.formatName(name());
    }
    
    public static Optional<SignCommandType> fromName(@NotNull String name) {
        String commandTypeBName = StringUtils.normalize(name);
        for (SignCommandType commandTypeA : values()) {
            String commandTypeAName = StringUtils.normalize(commandTypeA.getName());
            if (commandTypeAName.equals(commandTypeBName)) {
                return Optional.of(commandTypeA);
            }
        }
        return Optional.empty();
    }
}