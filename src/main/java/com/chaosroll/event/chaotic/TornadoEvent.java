package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class TornadoEvent extends BaseEvent {
    @Override public String getId() { return "tornado"; }
    @Override public String getDisplayName() { return "Торнадо"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 6; }
    @Override public boolean isGlobal() { return true; }
    @Override public int getDurationTicks() { return 200; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        ServerLevel world = context.world();

        world.setWeatherParameters(0, 6000, true, true);

        for (int i = 0; i < 40; i++) {
            int delay = i * 3;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                ServerPlayer center = srv.getPlayerList().getPlayer(initiator.getUUID());
                if (center == null) return;
                ServerLevel lvl = center.serverLevel();
                double angle = lvl.random.nextDouble() * Math.PI * 2.0;
                double dist = 4 + lvl.random.nextDouble() * 21;
                double x = center.getX() + Math.cos(angle) * dist;
                double z = center.getZ() + Math.sin(angle) * dist;
                double y = lvl.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(lvl);
                if (bolt != null) {
                    bolt.moveTo(x, y, z);
                    lvl.addFreshEntity(bolt);
                }
            });
        }

        AABB area = new AABB(
                initiator.getX() - 20, initiator.getY() - 5, initiator.getZ() - 20,
                initiator.getX() + 20, initiator.getY() + 30, initiator.getZ() + 20);
        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, area)) {
            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 80, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));
            double dx = entity.getX() - initiator.getX();
            double dz = entity.getZ() - initiator.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 0.1) len = 0.1;
            double tx = -dz / len * 0.6;
            double tz = dx / len * 0.6;
            entity.push(tx, 0.4, tz);
            if (entity instanceof ServerPlayer sp) {
                sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(sp));
            }
        }

        for (int i = 0; i < 200; i++) {
            double angle = i * 0.3;
            double r = 1 + (i / 30.0);
            double x = initiator.getX() + Math.cos(angle) * r;
            double z = initiator.getZ() + Math.sin(angle) * r;
            double y = initiator.getY() + (i / 8.0);
            world.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0, 0, 0);
        }

        world.playSound(null, initiator.getX(), initiator.getY(), initiator.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 4.0f, 0.5f);

        EventNotifyUtil.notifyAll(initiator, this, "ТОРНАДО! 40 блискавок + Levitation III!");
    }
}
