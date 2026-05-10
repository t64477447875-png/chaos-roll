package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

public class BatStormEvent extends BaseEvent {
    @Override public String getId() { return "bat_storm"; }
    @Override public String getDisplayName() { return "Шторм кажанів"; }
    @Override public String getDescription() { return "50 кажанів навколо тебе — нічого не видно"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        var rng = context.random();
        for (int i = 0; i < 50; i++) {
            double dx = (rng.nextDouble() - 0.5) * 6;
            double dy = (rng.nextDouble() - 0.5) * 4;
            double dz = (rng.nextDouble() - 0.5) * 6;
            var bat = EntityType.BAT.create(world);
            if (bat == null) continue;
            bat.moveTo(player.getX() + dx, player.getY() + dy, player.getZ() + dz, rng.nextFloat() * 360, 0);
            world.addFreshEntityWithPassengers(bat);
        }
        EventNotifyUtil.notifyPlayer(player, this, "50 кажанів навколо!");
    }
}
