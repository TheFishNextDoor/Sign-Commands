package fun.sunrisemc.sign_commands.sign_command;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.event.block.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum SignClickType {

    BOTH,
    LEFT,
    RIGHT;
    
    public static Optional<SignClickType> fromAction(@NonNull Action action) {
        switch (action) {
            case LEFT_CLICK_BLOCK:
                return Optional.of(LEFT);
            case RIGHT_CLICK_BLOCK:
                return Optional.of(RIGHT);
            default:
                return Optional.empty();
        }
    }

    public static Optional<@NonNull SignClickType> fromName(@NonNull String clickTypeBName) {
        for (SignClickType clickTypeA : values()) {
            String clickTypeAName = clickTypeA.name();
            if (clickTypeAName == null) {
                return Optional.empty();
            }
            if (normalizeName(clickTypeAName).equals(normalizeName(clickTypeBName))) {
                return Optional.ofNullable(clickTypeA);
            }
        }
        return Optional.empty();
    }

    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignClickType clickType : values()) {
            String clickTypeName = clickType.name();
            if (clickTypeName == null) {
                continue;
            }
            names.add(normalizeName(clickTypeName));
        }
        return names;
    }

    private static String normalizeName(@NonNull String name) {
        return name.trim().toLowerCase().replace(" ", "-").replace("_", "-");
    }
    
}
