package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FullRestoreEvent extends BaseEvent {
    @Override public String getId() { return "full_restore"; }
    @Override public String getDisplayName() { return "Повне відновлення"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 50; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setHealth(player.getMaxHealth());
        player.getFoodData().eat(20, 1.0f);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 4));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2));
        player.clearFire();
        EventNotifyUtil.notifyPlayer(player, this, "Повне HP, голод, Resistance V на 30с");
    }
}