package fun.sunrisemc.signcommands.utils;

import java.util.ArrayList;

import org.bukkit.block.sign.Side;

import org.jetbrains.annotations.NotNull;

import fun.sunrisemc.signcommands.sign.command.SignClickType;
import fun.sunrisemc.signcommands.sign.command.SignCommandType;

public class Names {

    @NotNull
    public static ArrayList<String> getSignClickTypeNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignClickType clickType : SignClickType.values()) {
            names.add(clickType.getName());
        }
        return names;
    }

    @NotNull
    public static ArrayList<String> getSignCommandTypeNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (SignCommandType commandType : SignCommandType.values()) {
            names.add(commandType.getName());
        }
        return names;
    }

    @NotNull
    public static ArrayList<String> getSideNames() {
        ArrayList<String> sides = new ArrayList<>();
        for (Side side : Side.values()) {
            sides.add(StringUtils.formatName(side.name()));
        }
        return sides;
    }
}