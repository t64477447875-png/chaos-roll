package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class DimensionSwapEvent extends BaseEvent {
    @Override public String getId() { return "dimension_swap"; }
    @Override public String getDisplayName() { return "Зсув виміру"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 3; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 4));
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
        SafeTeleportUtil.teleportRandom(player, 300);
        EventNotifyUtil.notifyPlayer(player, this, "Реальність змістилась...");
    }
}