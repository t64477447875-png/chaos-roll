package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GodModeEvent extends BaseEvent {
    @Override public String getId() { return "god_mode"; }
    @Override public String getDisplayName() { return "Режим бога"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 4));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 2));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
        player.setHealth(player.getMaxHealth());
        EventNotifyUtil.notifyPlayer(player, this, "Resistance V + Regen + Fire Res 60с");
    }
}