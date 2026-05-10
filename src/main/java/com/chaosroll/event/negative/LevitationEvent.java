package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class LevitationEvent extends BaseEvent {
    @Override public String getId() { return "levitation"; }
    @Override public String getDisplayName() { return "Левітація"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 2));
        EventNotifyUtil.notifyPlayer(player, this, "Левітація III — без парашута, готуйся до падіння");
    }
}