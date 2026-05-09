package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FogEvent extends BaseEvent {
    @Override public String getId() { return "fog"; }
    @Override public String getDisplayName() { return "Густий туман"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }
    @Override public int getDurationTicks() { return 400; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.DARKNESS, 400, 0));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Темрява на 20 сек");
    }
}