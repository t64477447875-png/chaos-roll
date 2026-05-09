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
