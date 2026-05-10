package com.chaosroll.event.coop;

import com.chaosroll.ChaosRollMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CoopTickHandler {

    public static final String NBT_HOT_POTATO = "chaosroll_hot_potato";
    public static final String NBT_DUEL_KIT = "chaosroll_duel_kit";

    private CoopTickHandler() {}

    public static void tick(MinecraftServer server) {
        tickSharedHealth(server);
        tickTwinFate(server);
        tickHotPotato(server);
        tickLifeline(server);
        tickSpeedrun(server);
        tickHungerGames(server);
        tickBerserker(server);
        tickExpiringMap(server, CoopState.BLOCK_ROULETTE);
        tickExpiringMap(server, CoopState.CURSED_DAMAGE);
        tickExpiringMap(server, CoopState.DOUBLE_DROPS);
        tickExpiringMap(server, CoopState.RANDOM_LOOT);
        tickExpiringMap(server, CoopState.MIDAS_TOUCH);
        tickPathBuilder(server);
        tickIronLung(server);
        tickMagnetCurse(server);
        tickEffectCasino(server);
        tickRocketBoots(server);
        tickStaticShock(server);
        tickDirectionLock(server);
        tickMorph(server);
    }

    private static void tickIronLung(MinecraftServer server) {
        if (CoopState.IRON_LUNG.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.IRON_LUNG.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            if (now >= e.getValue()) {
                it.remove();
                ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
                if (p != null) p.setAirSupply(p.getMaxAirSupply());
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            if (p.isInWater()) continue;
            int air = p.getAirSupply();
            p.setAirSupply(Math.max(-20, air - 4));
            if (p.getAirSupply() <= -20 && now % 20 == 0) {
                p.hurt(p.damageSources().drown(), 2.0f);
                p.setAirSupply(0);
            }
        }
    }

    private static void tickMagnetCurse(MinecraftServer server) {
        if (CoopState.MAGNET_CURSE.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.MAGNET_CURSE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            if (now >= e.getValue()) {
                it.remove();
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            if (now % 100 != 0) continue;
            ItemStack inHand = p.getMainHandItem();
            if (!inHand.isEmpty()) {
                p.drop(inHand.copy(), false, true);
                inHand.setCount(0);
                p.containerMenu.broadcastChanges();
            }
        }
    }

    private static void tickEffectCasino(MinecraftServer server) {
        if (CoopState.EFFECT_CASINO.isEmpty()) return;
        int now = server.getTickCount();
        java.util.Random rng = new java.util.Random();
        net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>[] pool =
                new net.minecraft.core.Holder[]{
                        MobEffects.MOVEMENT_SPEED, MobEffects.MOVEMENT_SLOWDOWN,
                        MobEffects.JUMP, MobEffects.WEAKNESS,
                        MobEffects.DIG_SPEED, MobEffects.DIG_SLOWDOWN,
                        MobEffects.DAMAGE_RESISTANCE, MobEffects.POISON,
                        MobEffects.REGENERATION, MobEffects.NIGHT_VISION,
                        MobEffects.GLOWING, MobEffects.HUNGER,
                        MobEffects.WATER_BREATHING, MobEffects.LUCK,
                        MobEffects.UNLUCK, MobEffects.SLOW_FALLING
                };
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.EFFECT_CASINO.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            if (now >= e.getValue()) {
                it.remove();
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            if (now % 100 != 0) continue;
            var picked = pool[rng.nextInt(pool.length)];
            int amp = rng.nextInt(2);
            p.addEffect(new MobEffectInstance(picked, 120, amp));
        }
    }

    private static void tickRocketBoots(MinecraftServer server) {
        if (CoopState.ROCKET_BOOTS.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.ROCKET_BOOTS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            if (now >= e.getValue()) {
                it.remove();
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            if (p.getDeltaMovement().y > 0.3 && !p.onGround()) {
                var dm = p.getDeltaMovement();
                p.setDeltaMovement(dm.x, Math.max(dm.y, 1.6), dm.z);
                p.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(p));
                p.fallDistance = 0;
            }
        }
    }

    private static void tickStaticShock(MinecraftServer server) {
        if (CoopState.STATIC_SHOCK.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.STATIC_SHOCK.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            if (now >= e.getValue()) {
                it.remove();
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            if (now % 100 != 0) continue;
            ServerLevel lvl = p.serverLevel();
            net.minecraft.world.entity.LightningBolt bolt =
                    net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(lvl);
            if (bolt != null) {
                bolt.moveTo(p.getX(), p.getY(), p.getZ());
                lvl.addFreshEntity(bolt);
            }
        }
    }

    private static void tickDirectionLock(MinecraftServer server) {
        if (CoopState.DIRECTION_LOCK.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, CoopState.DirectionLock>> it = CoopState.DIRECTION_LOCK.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CoopState.DirectionLock> e = it.next();
            CoopState.DirectionLock dl = e.getValue();
            if (now >= dl.endTick) {
                it.remove();
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            p.connection.teleport(p.getX(), p.getY(), p.getZ(), dl.lockedYaw, p.getXRot());
        }
    }

    private static void tickMorph(MinecraftServer server) {
        if (CoopState.MORPH.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, CoopState.MorphSession>> it = CoopState.MORPH.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CoopState.MorphSession> e = it.next();
            CoopState.MorphSession ms = e.getValue();
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (now >= ms.endTick || p == null) {
                it.remove();
                if (p != null) {
                    ServerLevel lvl = p.serverLevel();
                    var ent = lvl.getEntity(ms.mobUuid);
                    if (ent != null) ent.discard();
                    p.removeEffect(MobEffects.INVISIBILITY);
                } else {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        var ent = lvl.getEntity(ms.mobUuid);
                        if (ent != null) { ent.discard(); break; }
                    }
                }
                continue;
            }
            ServerLevel lvl = p.serverLevel();
            var ent = lvl.getEntity(ms.mobUuid);
            if (ent == null) {
                it.remove();
                p.removeEffect(MobEffects.INVISIBILITY);
                continue;
            }
            ent.moveTo(p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot());
            ent.setYHeadRot(p.getYHeadRot());
            ent.setOnGround(p.onGround());
        }
    }

    private static void tickPathBuilder(MinecraftServer server) {
        if (CoopState.PATH_BUILDER.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.PATH_BUILDER.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            if (now >= e.getValue()) {
                it.remove();
                continue;
            }
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null) continue;
            net.minecraft.core.BlockPos under = p.blockPosition().below();
            ServerLevel lvl = p.serverLevel();
            if (lvl.getBlockState(under).isAir() || lvl.getBlockState(under).canBeReplaced()) {
                lvl.setBlock(under, net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK.defaultBlockState(), 3);
            }
        }
    }

    private static void tickLifeline(MinecraftServer server) {
        if (CoopState.LIFELINE.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, CoopState.LifelineSession>> it = CoopState.LIFELINE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CoopState.LifelineSession> e = it.next();
            CoopState.LifelineSession s = e.getValue();
            ServerPlayer pa = server.getPlayerList().getPlayer(s.a);
            ServerPlayer pb = server.getPlayerList().getPlayer(s.b);
            if (now >= s.endTick) {
                it.remove();
                if (pa != null) pa.sendSystemMessage(Component.literal("[Chaos Roll] Прив'язка душ закінчилась."));
                if (pb != null) pb.sendSystemMessage(Component.literal("[Chaos Roll] Прив'язка душ закінчилась."));
                continue;
            }
            if (pa == null || pb == null) continue;
            if (pa.isDeadOrDying() && pb.isAlive()) {
                pb.kill();
                pb.sendSystemMessage(Component.literal("[Chaos Roll] Твій напарник помер — і ти теж."));
            } else if (pb.isDeadOrDying() && pa.isAlive()) {
                pa.kill();
                pa.sendSystemMessage(Component.literal("[Chaos Roll] Твій напарник помер — і ти теж."));
            }
        }
    }

    private static void tickSpeedrun(MinecraftServer server) {
        CoopState.SpeedrunSession s = CoopState.SPEEDRUN;
        if (s == null || s.ended) return;
        int now = server.getTickCount();

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            double dx = p.getX() - (s.targetX + 0.5);
            double dy = p.getY() - (s.targetY + 5);
            double dz = p.getZ() - (s.targetZ + 0.5);
            if (dx * dx + dy * dy + dz * dz < 9) {
                s.ended = true;
                p.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD));
                p.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE));
                p.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND, 8));
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal("[Chaos Roll] " + p.getName().getString() + " виграв speedrun!"), false);
                CoopState.SPEEDRUN = null;
                return;
            }
        }

        if (now >= s.endTick) {
            s.ended = true;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
            }
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] Ніхто не встиг! Slow IV всім."), false);
            CoopState.SPEEDRUN = null;
        }
    }

    private static void tickHungerGames(MinecraftServer server) {
        CoopState.HungerGamesSession s = CoopState.HUNGER_GAMES;
        if (s == null || s.ended) return;
        int now = server.getTickCount();
        if (now < s.endTick) return;

        s.ended = true;
        ServerPlayer winner = null;
        float best = -1f;
        int alive = 0;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (p.isAlive() && p.getHealth() > 0) {
                alive++;
                if (p.getHealth() > best) {
                    best = p.getHealth();
                    winner = p;
                }
            }
        }
        if (winner != null && alive >= 1) {
            winner.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_HELMET));
            winner.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE));
            winner.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_LEGGINGS));
            winner.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_BOOTS));
            winner.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD));
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] HUNGER GAMES winner: " + winner.getName().getString() + "!"), false);
        } else {
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] HUNGER GAMES: ніхто не виграв."), false);
        }
        CoopState.HUNGER_GAMES = null;
    }

    private static void tickBerserker(MinecraftServer server) {
        if (CoopState.BERSERKER.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Integer>> it = CoopState.BERSERKER.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
            if (p == null || now >= e.getValue()) {
                it.remove();
                continue;
            }
            if (now % 40 == 0) {
                p.hurt(p.damageSources().magic(), 1.0f);
            }
        }
    }

    private static void tickExpiringMap(MinecraftServer server, Map<UUID, Integer> map) {
        if (map.isEmpty()) return;
        int now = server.getTickCount();
        map.entrySet().removeIf(e -> now >= e.getValue());
    }

    private static void tickSharedHealth(MinecraftServer server) {
        if (CoopState.SHARED_HEALTH.isEmpty()) return;
        int now = server.getTickCount();

        List<CoopState.SharedHealthSession> seen = new ArrayList<>();
        for (CoopState.SharedHealthSession s : CoopState.SHARED_HEALTH.values()) {
            if (!seen.contains(s)) seen.add(s);
        }

        for (CoopState.SharedHealthSession s : seen) {
            ServerPlayer pa = server.getPlayerList().getPlayer(s.a);
            ServerPlayer pb = server.getPlayerList().getPlayer(s.b);
            if (pa == null || pb == null || now >= s.endTick) {
                CoopState.SHARED_HEALTH.remove(s.a);
                CoopState.SHARED_HEALTH.remove(s.b);
                if (pa != null) pa.sendSystemMessage(Component.literal("[Chaos Roll] Спільне здоров'я закінчилось."));
                if (pb != null) pb.sendSystemMessage(Component.literal("[Chaos Roll] Спільне здоров'я закінчилось."));
                continue;
            }

            float curA = pa.getHealth();
            float curB = pb.getHealth();
            float dropA = Math.max(0f, s.lastHpA - curA);
            float dropB = Math.max(0f, s.lastHpB - curB);
            if (dropA >= 0.5f) {
                float relay = dropA * 0.5f;
                pb.hurt(serverDamage(pb), relay);
                s.lastHpB = pb.getHealth();
            }
            if (dropB >= 0.5f) {
                float relay = dropB * 0.5f;
                pa.hurt(serverDamage(pa), relay);
                s.lastHpA = pa.getHealth();
            }
            s.lastHpA = pa.getHealth();
            s.lastHpB = pb.getHealth();
        }
    }

    private static DamageSource serverDamage(ServerPlayer p) {
        return p.damageSources().magic();
    }

    private static void tickTwinFate(MinecraftServer server) {
        if (CoopState.TWIN_FATE.isEmpty()) return;
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, CoopState.TwinFateSession>> it = CoopState.TWIN_FATE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CoopState.TwinFateSession> e = it.next();
            CoopState.TwinFateSession s = e.getValue();
            ServerPlayer pa = server.getPlayerList().getPlayer(s.a);
            ServerPlayer pb = server.getPlayerList().getPlayer(s.b);
            if (pa == null || pb == null || now >= s.endTick) {
                it.remove();
            }
        }
    }

    private static void tickHotPotato(MinecraftServer server) {
        if (CoopState.HOT_POTATO.isEmpty()) return;
        int now = server.getTickCount();

        Iterator<Map.Entry<UUID, CoopState.HotPotatoSession>> it = CoopState.HOT_POTATO.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, CoopState.HotPotatoSession> e = it.next();
            CoopState.HotPotatoSession s = e.getValue();
            ServerPlayer holder = findItemHolder(server);
            if (holder == null) {
                holder = server.getPlayerList().getPlayer(s.owner);
            }
            if (holder == null) {
                it.remove();
                continue;
            }

            int remaining = s.endTick - now;
            if (remaining <= 0) {
                triggerHotPotato(server, holder);
                stripHotPotato(server);
                it.remove();
                continue;
            }

            if (remaining > 0 && remaining % 20 == 0) {
                int seconds = remaining / 20;
                holder.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(
                        Component.literal("Курка-бомба!")));
                holder.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(
                        Component.literal(seconds + "с — кинь напарнику (Q)!")));
                ServerLevel sl = holder.serverLevel();
                sl.playSound(null, holder.getX(), holder.getY(), holder.getZ(),
                        SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.PLAYERS,
                        0.6f, 0.6f + (1.0f - remaining / 600.0f));
            }
        }
    }

    private static ServerPlayer findItemHolder(MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            Inventory inv = p.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (hasMarker(stack, NBT_HOT_POTATO)) return p;
            }
        }
        return null;
    }

    public static boolean hasMarker(ItemStack stack, String marker) {
        if (stack.isEmpty()) return false;
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return false;
        return data.copyTag().getBoolean(marker);
    }

    public static void applyMarker(ItemStack stack, String marker) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(marker, true);
        CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
        if (existing != null) {
            CompoundTag merged = existing.copyTag();
            merged.putBoolean(marker, true);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(merged));
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static void triggerHotPotato(MinecraftServer server, ServerPlayer holder) {
        try {
            holder.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            holder.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 0));
            holder.hurt(holder.damageSources().magic(), 4.0f);
            ServerLevel sl = holder.serverLevel();
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    holder.getX(), holder.getY() + 1, holder.getZ(), 1, 0, 0, 0, 0);
            sl.playSound(null, holder.getX(), holder.getY(), holder.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0f, 1.4f);
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("[Chaos Roll] Курка-бомба вибухнула в руках "
                            + holder.getName().getString() + "!"), false);
        } catch (Throwable err) {
            ChaosRollMod.LOGGER.warn("[Chaos Roll] Hot potato trigger failed: {}", err.getMessage());
        }
    }

    public static void stripHotPotato(MinecraftServer server) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            stripMarker(p, NBT_HOT_POTATO);
        }
    }

    public static void stripMarker(ServerPlayer player, String marker) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (hasMarker(stack, marker)) {
                inv.setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
