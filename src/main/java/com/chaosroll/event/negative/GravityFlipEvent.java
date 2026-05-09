package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.UUID;

public class GravityFlipEvent extends BaseEvent {
    @Override public String getId() { return "gravity_flip"; }
    @Override public String getDisplayName() { return "Інверсія гравітації"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        UUID id = player.getUUID();
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 5));
        ScheduledTaskManager.schedule(context.server(), 60, srv -> {
            ServerPlayer p = srv.getPlayerList().getPlayer(id);
            if (p != null) p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0));
        });
        EventNotifyUtil.notifyPlayer(player, this, "Гравітація зламалась");
    }
}