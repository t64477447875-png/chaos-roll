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

public class RandomBlockSwapEvent extends BaseEvent {
    private static final List<Block> POOL = List.of(
            Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK,
            Blocks.LAPIS_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.GLOWSTONE,
            Blocks.PUMPKIN, Blocks.MELON, Blocks.HAY_BLOCK
    );

    @Override public String getId() { return "random_block_swap"; }
    @Override public String getDisplayName() { return "Підміна блоків"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition();
        Map<BlockPos, BlockState> saved = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            int dx = context.random().nextInt(7) - 3;
            int dz = context.random().nextInt(7) - 3;
            BlockPos pos = origin.offset(dx, -1, dz);
            BlockState orig = level.getBlockState(pos);
            if (orig.isAir() || orig.getBlock() == Blocks.BEDROCK) continue;
            saved.put(pos.immutable(), orig);
            level.setBlockAndUpdate(pos, POOL.get(context.random().nextInt(POOL.size())).defaultBlockState());
        }
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) level.setBlockAndUpdate(entry.getKey(), entry.getValue());
        });
        EventNotifyUtil.notifyPlayer(player, this, "Блоки навколо змінились на 20с");
    }
}