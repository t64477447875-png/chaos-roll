package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class PowerSurgeEvent extends BaseEvent {
    @Override public String getId() { return "power_surge"; }
    @Override public String getDisplayName() { return "Сплеск сили"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
        EventNotifyUtil.notifyPlayer(player, this, "30с: Strength III + Resistance II + Regen II");
    }
}
