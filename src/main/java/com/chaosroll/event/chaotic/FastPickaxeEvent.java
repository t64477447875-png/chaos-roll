package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FastPickaxeEvent extends BaseEvent {
    @Override public String getId() { return "fast_pickaxe"; }
    @Override public String getDisplayName() { return "Швидка кірка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        context.player().addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 3));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Haste IV на 60с");
    }
}