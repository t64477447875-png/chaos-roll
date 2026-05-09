package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class PoisonEvent extends BaseEvent {
    @Override public String getId() { return "poison"; }
    @Override public String getDisplayName() { return "Отруєння"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 40; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 4));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 2));
        EventNotifyUtil.notifyPlayer(player, this, "Poison V + Hunger III на 30с");
    }
}
