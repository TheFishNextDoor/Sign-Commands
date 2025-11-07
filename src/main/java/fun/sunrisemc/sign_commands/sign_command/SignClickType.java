package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.event.block.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum SignClickType {

    ANY_CLICK,
    LEFT_CLICK,
    RIGHT_CLICK;

    public String getName() {
        return normalizeName(name());
    }
    
    public static Optional<SignClickType> fromAction(@NonNull Action action) {
        switch (action) {
            case LEFT_CLICK_BLOCK:
                return Optional.of(LEFT_CLICK);
            case RIGHT_CLICK_BLOCK:
                return Optional.of(RIGHT_CLICK);
            default:
                return Optional.empty();
        }
    }

    public static Optional<@NonNull SignClickType> fromName(@NonNull String name) {
        String clickTypeBName = normalizeName(name);
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

    private static String normalizeName(@NonNull String str) {
        return str.trim().toLowerCase().replace(" ", "-").replace("_", "-");
    }
}