package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class PacifistEvent extends BaseEvent {
    @Override public String getId() { return "pacifist"; }
    @Override public String getDisplayName() { return "Пацифіст"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 4));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600, 4));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 1));
        EventNotifyUtil.notifyPlayer(player, this, "30с — тільки тікати, дамаг ~0!");
    }
}
