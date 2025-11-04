package fun.sunrisemc.sign_commands.utils;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RayTrace {

    private static final double MAX_DISTANCE = 64.0;

    public static Optional<Block> block(@NonNull Player player) {
        RayTraceResult result = player.rayTraceBlocks(MAX_DISTANCE);
        return Optional.ofNullable(result.getHitBlock());
    }
}