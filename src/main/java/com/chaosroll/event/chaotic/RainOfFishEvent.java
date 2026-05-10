package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;

/**
 * Spawns 25 fish (cod / salmon / pufferfish / tropical) around the player at sky level. They fall,
 * flop on the ground, give some food and chaos.
 */
public class RainOfFishEvent extends BaseEvent {
    @Override public String getId() { return "rain_of_fish"; }
    @Override public String getDisplayName() { return "Рибний дощ"; }
    @Override public String getDescription() { return "З неба сипляться 25 рибин — їжі вистачить, ще й пуферфіш на додачу."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 16; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        var rng = context.random();

        @SuppressWarnings("unchecked")
        EntityType<? extends net.minecraft.world.entity.Entity>[] pool =
                (EntityType<? extends net.minecraft.world.entity.Entity>[]) new EntityType[]{
                        EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH
                };

        int spawned = 0;
        for (int i = 0; i < 25; i++) {
            var type = pool[rng.nextInt(pool.length)];
            var fish = type.create(world);
            if (fish == null) continue;
            double dx = (rng.nextDouble() - 0.5) * 8;
            double dz = (rng.nextDouble() - 0.5) * 8;
            fish.setPos(player.getX() + dx, player.getY() + 8, player.getZ() + dz);
            world.addFreshEntity(fish);
            spawned++;
        }
        EventNotifyUtil.notifyPlayer(player, this, "Дощ з " + spawned + " рибин!");
    }
}
