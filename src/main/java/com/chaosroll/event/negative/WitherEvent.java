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
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 600, 3));
        EventNotifyUtil.notifyPlayer(player, this, "Wither IV на 30 сек — пий молоко або молись");
    }
}
