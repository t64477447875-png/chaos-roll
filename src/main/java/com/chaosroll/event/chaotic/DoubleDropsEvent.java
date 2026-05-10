package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DoubleDropsEvent extends BaseEvent {
    @Override public String getId() { return "double_drops"; }
    @Override public String getDisplayName() { return "Подвійні дропи"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 1200;
        CoopState.DOUBLE_DROPS.put(player.getUUID(), endTick);
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 1));
        EventNotifyUtil.notifyPlayer(player, this, "60с — кожен блок дропає 2× луту!");
    }
}
