package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class MiningFrenzyEvent extends BaseEvent {
    @Override public String getId() { return "mining_frenzy"; }
    @Override public String getDisplayName() { return "Шахтарська лють"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 4));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0));
        EventNotifyUtil.notifyPlayer(player, this, "60с: Haste V + Speed II + Night Vision");
    }
}
