package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MiningFatigueEvent extends BaseEvent {
    @Override public String getId() { return "mining_fatigue"; }
    @Override public String getDisplayName() { return "Втома шахтаря"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 40; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 2));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Mining Fatigue III на 30 сек");
    }
}