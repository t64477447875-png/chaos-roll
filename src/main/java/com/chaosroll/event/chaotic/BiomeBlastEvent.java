package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeBlastEvent extends BaseEvent {
    private static final List<List<Block>> THEMES = List.of(
            List.of(Blocks.SAND, Blocks.SANDSTONE, Blocks.RED_SAND),
            List.of(Blocks.SNOW_BLOCK, Blocks.PACKED_ICE, Blocks.BLUE_ICE),
            List.of(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, Blocks.MOSS_BLOCK),
            List.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.MAGMA_BLOCK),
            List.of(Blocks.END_STONE, Blocks.PURPUR_BLOCK, Blocks.OBSIDIAN)
    );

    @Override public String getId() { return "biome_blast"; }
    @Override public String getDisplayName() { return "Біомний вибух"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition().below();
        List<Block> theme = THEMES.get(context.random().nextInt(THEMES.size()));
        Map<BlockPos, BlockState> saved = new HashMap<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = origin.offset(dx, 0, dz);
                BlockState orig = level.getBlockState(pos);
                if (orig.isAir() || orig.getBlock() == Blocks.BEDROCK) continue;
                saved.put(pos.immutable(), orig);
                level.setBlockAndUpdate(pos, theme.get(context.random().nextInt(theme.size())).defaultBlockState());
            }
        }
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) level.setBlockAndUpdate(entry.getKey(), entry.getValue());
        });
        EventNotifyUtil.notifyPlayer(player, this, "Підлога змінила біом");
    }
}