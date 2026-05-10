package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

/**
 * Spawns 30 ladders going straight up from the player's position. Tempting climb to nowhere.
 */
public class StairSkyEvent extends BaseEvent {
    @Override public String getId() { return "stair_sky"; }
    @Override public String getDisplayName() { return "Сходи в небо"; }
    @Override public String getDescription() { return "Спавняться 30 драбинок прямо вгору. Лізь за нагородою (або пасткою)."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 14; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        BlockPos start = player.blockPosition();
        var ladderState = Blocks.LADDER.defaultBlockState()
                .setValue(net.minecraft.world.level.block.LadderBlock.FACING, player.getDirection().getOpposite());
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
        // Place a wall behind so ladders attach.
        for (int y = 1; y <= 30; y++) {
            mut.set(start.getX() + player.getDirection().getStepX(),
                    start.getY() + y,
                    start.getZ() + player.getDirection().getStepZ());
            if (world.getBlockState(mut).isAir() || world.getBlockState(mut).canBeReplaced()) {
                world.setBlock(mut, Blocks.STONE.defaultBlockState(), 3);
            }
            BlockPos ladderPos = mut.relative(player.getDirection().getOpposite()).immutable();
            if (world.getBlockState(ladderPos).isAir() || world.getBlockState(ladderPos).canBeReplaced()) {
                world.setBlock(ladderPos, ladderState, 3);
            }
        }
        EventNotifyUtil.notifyPlayer(player, this, "Сходи в небо — лізь!");
    }
}
