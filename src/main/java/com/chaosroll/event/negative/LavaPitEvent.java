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

public class LavaPitEvent extends BaseEvent {
    @Override public String getId() { return "lava_pit"; }
    @Override public String getDisplayName() { return "Лавова яма"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }
    @Override public int getDurationTicks() { return 200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition().below();
        Map<BlockPos, BlockState> saved = new HashMap<>();
        BlockState lava = Blocks.LAVA.defaultBlockState();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy >= -2; dy--) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    saved.put(pos.immutable(), level.getBlockState(pos));
                    level.setBlockAndUpdate(pos, lava);
                }
            }
        }
        player.setRemainingFireTicks(160);
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) {
                level.setBlockAndUpdate(entry.getKey(), entry.getValue());
            }
        });
        EventNotifyUtil.notifyPlayer(player, this, "Лавова яма під ногами! 10с щоб вистрибнути");
    }
}
