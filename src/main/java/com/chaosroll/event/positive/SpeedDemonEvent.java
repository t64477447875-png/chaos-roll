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
    @Override public int getWeight() { return 25; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
        context.player().addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 0));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Speed II + Jump I на 30 сек");
    }
}