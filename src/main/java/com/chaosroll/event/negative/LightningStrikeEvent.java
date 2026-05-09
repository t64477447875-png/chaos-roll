package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

import java.util.UUID;

public class LightningStrikeEvent extends BaseEvent {
    @Override public String getId() { return "lightning_strike"; }
    @Override public String getDisplayName() { return "Удар блискавки"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        UUID id = player.getUUID();
        for (int i = 0; i < 5; i++) {
            int delay = i * 8;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                ServerPlayer target = srv.getPlayerList().getPlayer(id);
                if (target == null) return;
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(target.serverLevel());
                if (bolt == null) return;
                double dx = (target.getRandom().nextDouble() - 0.5) * 4;
                double dz = (target.getRandom().nextDouble() - 0.5) * 4;
                bolt.moveTo(target.getX() + dx, target.getY(), target.getZ() + dz);
                target.serverLevel().addFreshEntity(bolt);
            });
        }
        EventNotifyUtil.notifyPlayer(player, this, "5 блискавок підряд!");
    }
}
