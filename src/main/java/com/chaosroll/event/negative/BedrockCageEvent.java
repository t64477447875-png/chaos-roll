package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BedrockCageEvent extends BaseEvent {
    @Override public String getId() { return "bedrock_cage"; }
    @Override public String getDisplayName() { return "Клітка з bedrock"; }
    @Override public String getDescription() { return "30с замкнений у 3×3×3 куб бедроку"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        BlockPos feet = player.blockPosition();
        List<BlockPos> placed = new ArrayList<>();
        List<BlockState> oldStates = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    if (dy == 0 || dy == 1) {
                        if (dx == 0 && dz == 0) continue;
                    }
                    BlockPos pos = feet.offset(dx, dy, dz);
                    placed.add(pos);
                    oldStates.add(world.getBlockState(pos));
                    world.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                }
            }
        }
        EventNotifyUtil.notifyPlayer(player, this, "Клітка з bedrock на 30с!");

        ScheduledTaskManager.schedule(context.server(), 600, srv -> {
            for (int i = 0; i < placed.size(); i++) {
                BlockPos p = placed.get(i);
                if (world.getBlockState(p).is(Blocks.BEDROCK)) {
                    world.setBlock(p, oldStates.get(i), 3);
                }
            }
        });
    }
}
