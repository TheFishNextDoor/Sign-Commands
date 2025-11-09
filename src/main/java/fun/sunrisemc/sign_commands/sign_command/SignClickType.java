package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.event.block.Action;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.sign_commands.utils.StringUtils;

public enum SignClickType {

    ANY_CLICK,
    LEFT_CLICK,
    RIGHT_CLICK;

    public String getName() {
        String name = name();
        if (name == null) {
            return "";
        }
        return StringUtils.normalize(name);
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
            if (clickTypeA.getName().equals(clickTypeBName)) {
                return Optional.of(clickTypeA);
            }
        }
        return Optional.empty();
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignClickType clickType : values()) {
            names.add(clickType.getName());
        }
        return names;
    }
}