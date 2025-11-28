package fun.sunrisemc.signcommands.utils;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class BlockUtils {

    public static Optional<Block> rayTraceBlock(@NotNull Player player) {
        RayTraceResult result = player.rayTraceBlocks(64.0);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.getHitBlock());
    }

    public static Optional<Sign> rayTraceSign(@NotNull Player player) {
        Optional<Block> block = rayTraceBlock(player);
        if (block.isEmpty()) {
            return Optional.empty();
        }

        BlockState state = block.get().getState();
        if (!(state instanceof Sign)) {
            return Optional.empty();
        }

        return Optional.of((Sign) state);
    }

    public static boolean isSign(@NotNull Block block) {
        BlockState state = block.getState();
        return state instanceof Sign;
    }
}