package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class LavaPitEvent extends BaseEvent {
    @Override public String getId() { return "lava_pit"; }
    @Override public String getDisplayName() { return "Лавова яма"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setRemainingFireTicks(160);
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0));
        EventNotifyUtil.notifyPlayer(player, this, "Гарячі бризки лави! 8с горіння");
    }
}