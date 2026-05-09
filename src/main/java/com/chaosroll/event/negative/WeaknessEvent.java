package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class WeaknessEvent extends BaseEvent {
    @Override public String getId() { return "weakness"; }
    @Override public String getDisplayName() { return "Слабкість"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 40; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 2));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Weakness III на 30 сек");
    }
}