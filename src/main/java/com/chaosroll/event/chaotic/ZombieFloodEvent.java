package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

/**
 * Spawns a wave of 12 zombies in a ring around the player. Each carries a random tool. Pure
 * survival horror moment.
 */
public class ZombieFloodEvent extends BaseEvent {
    @Override public String getId() { return "zombie_flood"; }
    @Override public String getDisplayName() { return "Зомбі-потоп"; }
    @Override public String getDescription() { return "12 зомбі формують кільце навколо тебе. Готуйся до бою."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI * 2 * i) / 12;
            double dist = 5;
            double x = player.getX() + Math.cos(angle) * dist;
            double z = player.getZ() + Math.sin(angle) * dist;
            Zombie z1 = EntityType.ZOMBIE.create(world);
            if (z1 == null) continue;
            z1.setPos(x, player.getY(), z);
            z1.setTarget(player);
            world.addFreshEntity(z1);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Кільце з 12 зомбі! Готуйся до бою.");
    }
}
