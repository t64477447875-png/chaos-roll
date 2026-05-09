package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BrittleBonesEvent extends BaseEvent {
    @Override public String getId() { return "brittle_bones"; }
    @Override public String getDisplayName() { return "Крихкі кістки"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 4));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 1));
        EventNotifyUtil.notifyPlayer(player, this, "Weakness V + Slowness II на 30 сек");
    }
}