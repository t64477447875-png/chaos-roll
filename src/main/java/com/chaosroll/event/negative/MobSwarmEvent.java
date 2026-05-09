package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

public class MobSwarmEvent extends BaseEvent {
    @Override public String getId() { return "mob_swarm"; }
    @Override public String getDisplayName() { return "Орда зомбі"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 6; i++) {
            Zombie z = EntityType.ZOMBIE.create(context.world());
            if (z == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 8;
            double dz = (context.random().nextDouble() - 0.5) * 8;
            z.setPos(player.getX() + dx, player.getY(), player.getZ() + dz);
            context.world().addFreshEntity(z);
        }
        EventNotifyUtil.notifyPlayer(player, this, "6 зомбі поруч!");
    }
}