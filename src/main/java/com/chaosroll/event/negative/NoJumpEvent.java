package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NoJumpEvent extends BaseEvent {
    @Override public String getId() { return "no_jump"; }
    @Override public String getDisplayName() { return "Без стрибків"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 4));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Slowness V на 30 сек — не стрибнеш");
    }
}