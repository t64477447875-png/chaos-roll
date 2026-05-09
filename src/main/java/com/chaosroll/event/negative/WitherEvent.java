package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class WitherEvent extends BaseEvent {
    @Override public String getId() { return "wither"; }
    @Override public String getDisplayName() { return "Висихання"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }
    @Override public int getDurationTicks() { return 200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setHealth(player.getMaxHealth());
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 0));
        EventNotifyUtil.notifyPlayer(player, this, "Wither I на 10 сек (HP відновлено)");
    }
}