package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class InfiniteWaterEvent extends BaseEvent {
    @Override public String getId() { return "infinite_water"; }
    @Override public String getDisplayName() { return "Потоп"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition().below();
        Map<BlockPos, BlockState> saved = new HashMap<>();
        BlockState water = Blocks.WATER.defaultBlockState();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = origin.offset(dx, 1, dz);
                BlockState orig = level.getBlockState(pos);
                if (!orig.canBeReplaced()) continue;
                saved.put(pos.immutable(), orig);
                level.setBlockAndUpdate(pos, water);
            }
        }
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 600, 0));
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) level.setBlockAndUpdate(entry.getKey(), entry.getValue());
        });
        EventNotifyUtil.notifyPlayer(player, this, "Вода навколо + Water Breathing 30с");
    }
}