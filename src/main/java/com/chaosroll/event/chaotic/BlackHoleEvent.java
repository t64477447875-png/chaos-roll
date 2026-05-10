package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class BlackHoleEvent extends BaseEvent {
    @Override public String getId() { return "black_hole"; }
    @Override public String getDisplayName() { return "Чорна діра"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 6; }
    @Override public int getDurationTicks() { return 200; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        EventNotifyUtil.notifyAll(player, this, "Чорна діра! 10с — все притягується до " + player.getName().getString());
        for (int t = 0; t < 200; t += 4) {
            ScheduledTaskManager.schedule(context.server(), t, srv -> {
                ServerPlayer p = srv.getPlayerList().getPlayer(player.getUUID());
                if (p == null) return;
                ServerLevel lvl = p.serverLevel();
                AABB area = new AABB(
                        p.getX() - 25, p.getY() - 10, p.getZ() - 25,
                        p.getX() + 25, p.getY() + 25, p.getZ() + 25);
                DamageSource src = lvl.damageSources().magic();
                for (Entity e : lvl.getEntities(p, area)) {
                    double dx = p.getX() - e.getX();
                    double dy = p.getY() + 1 - e.getY();
                    double dz = p.getZ() - e.getZ();
                    double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (d < 0.5) continue;
                    double pull = 0.6;
                    e.push(dx / d * pull, dy / d * pull, dz / d * pull);
                    if (e instanceof ServerPlayer sp) {
                        sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(sp));
                    }
                    if (e instanceof net.minecraft.world.entity.LivingEntity le && d < 4) {
                        le.hurt(src, 1.0f);
                    }
                }
                lvl.sendParticles(ParticleTypes.PORTAL, p.getX(), p.getY() + 1, p.getZ(), 50, 1.0, 1.0, 1.0, 0.5);
                lvl.sendParticles(ParticleTypes.SMOKE, p.getX(), p.getY() + 1, p.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
            });
        }
    }
}
