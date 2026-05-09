package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GodModeEvent extends BaseEvent {
    @Override public String getId() { return "god_mode"; }
    @Override public String getDisplayName() { return "Режим бога"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));
        EventNotifyUtil.notifyPlayer(player, this, "Resistance II + Regen 30с");
    }
}