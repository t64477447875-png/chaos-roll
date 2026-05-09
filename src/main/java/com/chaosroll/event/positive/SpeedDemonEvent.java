package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class SpeedDemonEvent extends BaseEvent {
    @Override public String getId() { return "speed_demon"; }
    @Override public String getDisplayName() { return "Швидкісний демон"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 60; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2));
        context.player().addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 1));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Speed III + Jump II на 60 сек");
    }
}