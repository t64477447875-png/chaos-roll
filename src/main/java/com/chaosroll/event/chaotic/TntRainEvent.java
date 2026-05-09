package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;

public class TntRainEvent extends BaseEvent {
    @Override public String getId() { return "tnt_rain"; }
    @Override public String getDisplayName() { return "Дощ з ТНТ"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setHealth(player.getMaxHealth());
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4));
        for (int i = 0; i < 3; i++) {
            PrimedTnt tnt = EntityType.TNT.create(context.world());
            if (tnt == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 12;
            double dz = (context.random().nextDouble() - 0.5) * 12;
            tnt.setPos(player.getX() + dx, player.getY() + 12, player.getZ() + dz);
            tnt.setFuse(80 + context.random().nextInt(40));
            context.world().addFreshEntity(tnt);
        }
        EventNotifyUtil.notifyPlayer(player, this, "TNT падає! Resistance V на 10с — біжи!");
    }
}