package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class GlassFloorEvent extends BaseEvent {
    @Override public String getId() { return "glass_floor"; }
    @Override public String getDisplayName() { return "Скляна підлога"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition().below();
        Map<BlockPos, BlockState> saved = new HashMap<>();
        BlockState glass = Blocks.GLASS.defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos pos = origin.offset(dx, 0, dz);
                saved.put(pos.immutable(), level.getBlockState(pos));
                level.setBlockAndUpdate(pos, glass);
            }
        }
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) {
                level.setBlockAndUpdate(entry.getKey(), entry.getValue());
            }
        });
        EventNotifyUtil.notifyPlayer(player, this, "Підлога стала склом на 20с");
    }
}