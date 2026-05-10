package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class TotalDarknessEvent extends BaseEvent {
    @Override public String getId() { return "total_darkness"; }
    @Override public String getDisplayName() { return "Темрява"; }
    @Override public String getDescription() { return "45с Blindness X + Mining Fatigue III"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 900; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 900, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 900, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 900, 2));
        EventNotifyUtil.notifyPlayer(player, this, "45с — повна темрява + Mining Fatigue III");
    }
}
