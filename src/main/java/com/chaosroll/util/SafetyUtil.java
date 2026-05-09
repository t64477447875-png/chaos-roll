package com.chaosroll.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class SafetyUtil {

    private SafetyUtil() {}

    public static boolean isSafePosition(ServerLevel level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        BlockState at = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        if (!below.isSolid()) return false;
        if (!at.isAir() && !at.canBeReplaced()) return false;
        if (!above.isAir() && !above.canBeReplaced()) return false;
        if (!below.getFluidState().isEmpty()) return false;
        if (!at.getFluidState().isEmpty()) return false;
        return true;
    }

    public static BlockPos findSafeY(ServerLevel level, int x, int z) {
        int top = level.getMaxBuildHeight() - 1;
        int bottom = level.getMinBuildHeight() + 5;
        for (int y = top; y > bottom; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (isSafePosition(level, pos)) {
                return pos;
            }
        }
        return null;
    }
}