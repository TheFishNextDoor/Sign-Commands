package fun.sunrisemc.signcommands.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;

import org.jetbrains.annotations.NotNull;

public class SignUtils {

    public static boolean isSign(@NotNull Block block) {
        BlockState state = block.getState();
        return state instanceof Sign;
    }

    public static int getLineCount(@NotNull Sign sign, @NotNull Side side) {
        return sign.getSide(side).getLines().length;
    }
}