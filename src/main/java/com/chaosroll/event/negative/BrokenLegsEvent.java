package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BrokenLegsEvent extends BaseEvent {
    @Override public String getId() { return "broken_legs"; }
    @Override public String getDisplayName() { return "Зламані ноги"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1200, 4));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1200, 1));
        EventNotifyUtil.notifyPlayer(player, this, "Slowness V на 60 сек");
    }
}