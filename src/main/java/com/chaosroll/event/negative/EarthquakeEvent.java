package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;

import java.util.UUID;

public class EarthquakeEvent extends BaseEvent {
    @Override public String getId() { return "earthquake"; }
    @Override public String getDisplayName() { return "Землетрус"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 22; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        UUID id = player.getUUID();
        ServerLevel world = context.world();
        BlockPos origin = player.blockPosition();
        int radius = 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos pos = origin.offset(dx, -1, dz);
                if (!world.getBlockState(pos).isAir()) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
        ScheduledTaskManager.schedule(context.server(), 60, srv -> {
            ServerPlayer p = srv.getPlayerList().getPlayer(id);
            if (p == null) return;
            ServerLevel lvl = p.serverLevel();
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = origin.offset(dx, -1, dz);
                    if (lvl.getBlockState(pos).isAir()) {
                        lvl.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
                    }
                }
            }
        });
        EventNotifyUtil.notifyPlayer(player, this, "Земля під ногами зникла!");
    }
}
