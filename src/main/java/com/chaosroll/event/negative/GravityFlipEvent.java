package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class GravityFlipEvent extends BaseEvent {
    @Override public String getId() { return "gravity_flip"; }
    @Override public String getDisplayName() { return "Інверсія гравітації"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 7));
        EventNotifyUtil.notifyPlayer(player, this, "Гравітація зламалась — без парашута");
    }
}