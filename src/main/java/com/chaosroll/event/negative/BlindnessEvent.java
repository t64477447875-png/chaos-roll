package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BlindnessEvent extends BaseEvent {
    @Override public String getId() { return "blindness"; }
    @Override public String getDisplayName() { return "Сліпота"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 40; }
    @Override public int getDurationTicks() { return 300; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Сліпота на 15 сек");
    }
}