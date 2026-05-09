package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class PoisonEvent extends BaseEvent {
    @Override public String getId() { return "poison"; }
    @Override public String getDisplayName() { return "Отруєння"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 40; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.POISON, 400, 1));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Poison II на 20 сек");
    }
}