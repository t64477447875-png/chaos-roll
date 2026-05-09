package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.UUID;

public class GravityInverseEvent extends BaseEvent {
    @Override public String getId() { return "gravity_inverse"; }
    @Override public String getDisplayName() { return "Інверсія гравітації"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        UUID id = player.getUUID();
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 7));
        ScheduledTaskManager.schedule(context.server(), 100, srv -> {
            ServerPlayer p = srv.getPlayerList().getPlayer(id);
            if (p != null) p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));
        });
        EventNotifyUtil.notifyPlayer(player, this, "Сильна левітація → повільне падіння");
    }
}