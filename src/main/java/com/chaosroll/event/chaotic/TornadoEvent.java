package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
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
                double y = lvl.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(lvl);
                if (bolt != null) {
                    bolt.moveTo(x, y, z);
                    lvl.addFreshEntity(bolt);
                }
            });
        }

        AABB entityArea = new AABB(
                initiator.getX() - 25, initiator.getY() - 5, initiator.getZ() - 25,
                initiator.getX() + 25, initiator.getY() + 40, initiator.getZ() + 25);
        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, entityArea)) {
            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 4));
            double dx = entity.getX() - initiator.getX();
            double dz = entity.getZ() - initiator.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 0.1) len = 0.1;
            double tx = -dz / len * 1.0;
            double tz = dx / len * 1.0;
            entity.push(tx, 0.6, tz);
            if (entity instanceof ServerPlayer sp) {
                sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(sp));
            }
        }

        int blocksLifted = 0;
        int radius = 12;
        int maxBlocks = 35;
        BlockPos center = initiator.blockPosition();
        java.util.List<BlockPos> candidates = new java.util.ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius) continue;
                int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, center.getX() + dx, center.getZ() + dz);
                for (int dy = 0; dy < 3; dy++) {
                    BlockPos pos = new BlockPos(center.getX() + dx, topY - 1 - dy, center.getZ() + dz);
                    BlockState st = world.getBlockState(pos);
                    if (canLift(st)) {
                        candidates.add(pos);
                    }
                }
            }
        }
        java.util.Collections.shuffle(candidates, new java.util.Random(world.getGameTime()));

        for (BlockPos pos : candidates) {
            if (blocksLifted >= maxBlocks) break;
            BlockState st = world.getBlockState(pos);
            if (!canLift(st)) continue;
            double dx = pos.getX() + 0.5 - initiator.getX();
            double dz = pos.getZ() + 0.5 - initiator.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 0.1) len = 0.1;
            double tx = -dz / len * 0.7 + (world.random.nextDouble() - 0.5) * 0.2;
            double tz = dx / len * 0.7 + (world.random.nextDouble() - 0.5) * 0.2;
            double upY = 0.8 + world.random.nextDouble() * 0.4;
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            FallingBlockEntity fbe = FallingBlockEntity.fall(world, pos, st);
            fbe.setDeltaMovement(tx, upY, tz);
            fbe.setHurtsEntities(2.0f, 20);
            fbe.time = 1;
            blocksLifted++;
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

        EventNotifyUtil.notifyAll(initiator, this, "ТОРНАДО! Levitation V + " + blocksLifted + " блоків піднято!");
    }

    private static boolean canLift(BlockState st) {
        if (st.isAir()) return false;
        if (st.hasBlockEntity()) return false;
        if (st.getDestroySpeed(null, null) < 0) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.BEDROCK)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.OBSIDIAN)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.ANCIENT_DEBRIS)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.END_PORTAL_FRAME)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.END_PORTAL)) return false;
        if (st.is(net.minecraft.world.level.block.Blocks.NETHER_PORTAL)) return false;
        return true;
    }
}
