package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class FallFromSkyEvent extends BaseEvent {
    @Override public String getId() { return "fall_from_sky"; }
    @Override public String getDisplayName() { return "Падіння з неба"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));
        player.teleportTo(player.getX(), 250, player.getZ());
        EventNotifyUtil.notifyPlayer(player, this, "Y=250 — насолоджуйся видом");
    }
}