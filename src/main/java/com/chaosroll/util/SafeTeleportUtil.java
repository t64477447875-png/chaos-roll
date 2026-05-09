package com.chaosroll.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

public final class SafeTeleportUtil {

    private SafeTeleportUtil() {}

    public static boolean teleportRandom(ServerPlayer player, int radius) {
        ServerLevel level = player.serverLevel();
        RandomSource rng = player.getRandom();
        BlockPos origin = player.blockPosition();
        for (int attempt = 0; attempt < 32; attempt++) {
            int dx = rng.nextInt(radius * 2 + 1) - radius;
            int dz = rng.nextInt(radius * 2 + 1) - radius;
            int targetX = origin.getX() + dx;
            int targetZ = origin.getZ() + dz;
            BlockPos pos = SafetyUtil.findSafeY(level, targetX, targetZ);
            if (pos != null) {
                player.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                return true;
            }
        }
        return false;
    }
}