package fun.sunrisemc.signcommands.sign.command;

import java.util.Optional;

import org.bukkit.event.block.Action;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.utils.StringUtils;

public enum SignClickType {

    ANY_CLICK,
    LEFT_CLICK,
    RIGHT_CLICK;

    @NotNull
    public String getName() {
        return StringUtils.formatName(name());
    }
    
    public static Optional<SignClickType> fromAction(@NotNull Action action) {
        switch (action) {
            case LEFT_CLICK_BLOCK:
                return Optional.of(LEFT_CLICK);
            case RIGHT_CLICK_BLOCK:
                return Optional.of(RIGHT_CLICK);
            default:
                return Optional.empty();
        }
    }

    public static Optional<SignClickType> fromName(@NotNull String name) {
        String clickTypeBName = StringUtils.normalize(name);
        for (SignClickType clickTypeA : values()) {
            String clickTypeAName = StringUtils.normalize(clickTypeA.getName());
            if (clickTypeAName.equals(clickTypeBName)) {
                return Optional.of(clickTypeA);
            }
        }
        return Optional.empty();
    }
}