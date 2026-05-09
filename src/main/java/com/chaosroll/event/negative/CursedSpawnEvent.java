package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class CursedSpawnEvent extends BaseEvent {
    @Override public String getId() { return "cursed_spawn"; }
    @Override public String getDisplayName() { return "Прокляте відродження"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }
    @Override public int getDurationTicks() { return 200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setHealth(1.0f);
        player.getFoodData().setFoodLevel(2);
        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
        EventNotifyUtil.notifyPlayer(player, this, "1 HP + Wither II + Slow III. Виживай.");
    }
}
