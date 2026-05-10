package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

public class MeteorStormEvent extends BaseEvent {
    @Override public String getId() { return "meteor_storm"; }
    @Override public String getDisplayName() { return "Метеоритний дощ"; }
    @Override public String getDescription() { return "8 великих фаєрболів падають з неба під різними кутами."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        var rng = context.random();

        for (int i = 0; i < 8; i++) {
            double dx = (rng.nextDouble() - 0.5) * 30;
            double dz = (rng.nextDouble() - 0.5) * 30;
            double startX = player.getX() + dx;
            double startY = player.getY() + 50;
            double startZ = player.getZ() + dz;

            Vec3 dir = new Vec3(player.getX() - startX, player.getY() - startY, player.getZ() - startZ).normalize();
            LargeFireball fireball = new LargeFireball(world, player, dir.scale(0.05), 2);
            fireball.setPos(startX, startY, startZ);
            world.addFreshEntity(fireball);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Метеори летять з неба — 8 шт.!");
    }
}
