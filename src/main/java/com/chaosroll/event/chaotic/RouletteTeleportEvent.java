package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class RouletteTeleportEvent extends BaseEvent {
    @Override public String getId() { return "roulette_teleport"; }
    @Override public String getDisplayName() { return "Рулетка телепорту"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 25; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        boolean lucky = context.random().nextBoolean();
        int radius = lucky ? 50 : 300;
        boolean ok = SafeTeleportUtil.teleportRandom(player, radius);
        if (!ok) {
            EventNotifyUtil.notifyPlayer(player, this, "Телепорт не вдався");
            return;
        }
        if (lucky) {
            player.setHealth(player.getMaxHealth());
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
            EventNotifyUtil.notifyPlayer(player, this, "Пощастило! Близький телепорт + Regen");
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
            EventNotifyUtil.notifyPlayer(player, this, "Не пощастило: 300 блоків + Slow II");
        }
    }
}
