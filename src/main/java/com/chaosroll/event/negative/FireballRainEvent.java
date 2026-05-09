package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class FireballRainEvent extends BaseEvent {
    @Override public String getId() { return "fireball_rain"; }
    @Override public String getDisplayName() { return "Вогняний дощ"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        UUID id = player.getUUID();
        for (int i = 0; i < 8; i++) {
            int delay = i * 6;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                ServerPlayer target = srv.getPlayerList().getPlayer(id);
                if (target == null) return;
                double dx = (target.getRandom().nextDouble() - 0.5) * 6;
                double dz = (target.getRandom().nextDouble() - 0.5) * 6;
                Vec3 motion = new Vec3(0, -1, 0);
                LargeFireball fireball = new LargeFireball(
                        target.serverLevel(), target, motion, 1);
                fireball.setPos(target.getX() + dx, target.getY() + 25, target.getZ() + dz);
                target.serverLevel().addFreshEntity(fireball);
            });
        }
        EventNotifyUtil.notifyPlayer(player, this, "8 фаєрболів падають з неба!");
    }
}
