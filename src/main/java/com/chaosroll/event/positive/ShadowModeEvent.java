package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ShadowModeEvent extends BaseEvent {
    @Override public String getId() { return "shadow_mode"; }
    @Override public String getDisplayName() { return "Режим тіні"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 20; }
    @Override public int getDurationTicks() { return 300; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Невидимість на 15 сек");
    }
}