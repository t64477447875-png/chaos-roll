package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperPartyEvent extends BaseEvent {
    @Override public String getId() { return "creeper_party"; }
    @Override public String getDisplayName() { return "Кріпер-вечірка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 5; i++) {
            Creeper c = EntityType.CREEPER.create(context.world());
            if (c == null) continue;
            double angle = (Math.PI * 2 * i) / 5;
            double r = 15;
            c.setPos(player.getX() + Math.cos(angle) * r, player.getY(), player.getZ() + Math.sin(angle) * r);
            context.world().addFreshEntity(c);
        }
        EventNotifyUtil.notifyPlayer(player, this, "5 кріперів навколо!");
    }
}