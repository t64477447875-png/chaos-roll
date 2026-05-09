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

public class ColorWorldEvent extends BaseEvent {
    private static final List<Block> WOOLS = List.of(
            Blocks.RED_WOOL, Blocks.BLUE_WOOL, Blocks.GREEN_WOOL, Blocks.YELLOW_WOOL,
            Blocks.PURPLE_WOOL, Blocks.PINK_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL,
            Blocks.CYAN_WOOL, Blocks.LIME_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.WHITE_WOOL
    );

    @Override public String getId() { return "color_world"; }
    @Override public String getDisplayName() { return "Кольоровий світ"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition();
        Map<BlockPos, BlockState> saved = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            int dx = context.random().nextInt(11) - 5;
            int dy = context.random().nextInt(5) - 2;
            int dz = context.random().nextInt(11) - 5;
            BlockPos pos = origin.offset(dx, dy, dz);
            BlockState orig = level.getBlockState(pos);
            if (!orig.isAir()) continue;
            saved.put(pos.immutable(), orig);
            level.setBlockAndUpdate(pos, WOOLS.get(context.random().nextInt(WOOLS.size())).defaultBlockState());
        }
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) level.setBlockAndUpdate(entry.getKey(), entry.getValue());
        });
        EventNotifyUtil.notifyPlayer(player, this, "Кольорові вовни навколо");
    }
}