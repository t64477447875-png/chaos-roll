package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class SuperJumpEvent extends BaseEvent {
    @Override public String getId() { return "super_jump"; }
    @Override public String getDisplayName() { return "Супер-стрибок"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 9));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 800, 0));
        EventNotifyUtil.notifyPlayer(player, this, "Jump X на 30с (+ Slow Falling)");
    }
}