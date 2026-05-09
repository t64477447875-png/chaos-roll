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

public class IceCageEvent extends BaseEvent {
    @Override public String getId() { return "ice_cage"; }
    @Override public String getDisplayName() { return "Льодяна клітка"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 300; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition();
        BlockPos[] cage = {
                origin.north(), origin.south(), origin.east(), origin.west(),
                origin.above().north(), origin.above().south(), origin.above().east(), origin.above().west(),
                origin.above(2)
        };
        Map<BlockPos, BlockState> saved = new HashMap<>();
        BlockState ice = Blocks.PACKED_ICE.defaultBlockState();
        for (BlockPos pos : cage) {
            saved.put(pos.immutable(), level.getBlockState(pos));
            level.setBlockAndUpdate(pos, ice);
        }
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) {
                level.setBlockAndUpdate(entry.getKey(), entry.getValue());
            }
        });
        EventNotifyUtil.notifyPlayer(player, this, "Заморожено на 15 секунд!");
    }
}