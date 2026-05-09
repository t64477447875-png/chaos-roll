package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class TreasureBlockEvent extends BaseEvent {
    private static final List<Block> POOL = List.of(
            Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.NETHERITE_BLOCK
    );

    @Override public String getId() { return "treasure_block"; }
    @Override public String getDisplayName() { return "Скарбовий блок"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 3; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        Block picked = POOL.get(context.random().nextInt(POOL.size()));
        BlockPos pos = player.blockPosition().above(2);
        context.world().setBlockAndUpdate(pos, picked.defaultBlockState());
        EventNotifyUtil.notifyPlayer(player, this, "Скарбовий блок над тобою!");
    }
}