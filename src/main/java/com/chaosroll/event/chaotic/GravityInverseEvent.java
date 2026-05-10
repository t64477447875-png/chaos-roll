package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GravityInverseEvent extends BaseEvent {
    @Override public String getId() { return "gravity_inverse"; }
    @Override public String getDisplayName() { return "Інверсія гравітації"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 9));
        EventNotifyUtil.notifyPlayer(player, this, "Сильна левітація — без парашута");
    }
}