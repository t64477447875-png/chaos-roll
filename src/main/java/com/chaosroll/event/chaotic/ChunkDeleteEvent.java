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

public class ChunkDeleteEvent extends BaseEvent {
    @Override public String getId() { return "chunk_delete"; }
    @Override public String getDisplayName() { return "Чанк зник!"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel level = context.world();
        BlockPos origin = player.blockPosition().below();
        Map<BlockPos, BlockState> saved = new HashMap<>();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy < 15; dy++) {
                    BlockPos pos = origin.offset(dx, -dy, dz);
                    BlockState orig = level.getBlockState(pos);
                    if (orig.isAir() || orig.getBlock() == Blocks.BEDROCK) continue;
                    saved.put(pos.immutable(), orig);
                    level.setBlockAndUpdate(pos, air);
                }
            }
        }
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0));
        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            for (var entry : saved.entrySet()) level.setBlockAndUpdate(entry.getKey(), entry.getValue());
        });
        EventNotifyUtil.notifyPlayer(player, this, "Земля зникла! 10с до повернення");
    }
}