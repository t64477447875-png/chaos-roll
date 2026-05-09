package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;

public class VillagerSwarmEvent extends BaseEvent {
    @Override public String getId() { return "villager_swarm"; }
    @Override public String getDisplayName() { return "Селянський рій"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 28; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 8; i++) {
            Villager v = EntityType.VILLAGER.create(context.world());
            if (v == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 6;
            double dz = (context.random().nextDouble() - 0.5) * 6;
            v.setPos(player.getX() + dx, player.getY(), player.getZ() + dz);
            context.world().addFreshEntity(v);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Прийшли 8 селян 'привітатись'");
    }
}