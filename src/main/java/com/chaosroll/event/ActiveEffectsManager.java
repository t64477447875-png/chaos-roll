package com.chaosroll.event;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.network.ActiveEffectsPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ActiveEffectsManager {

    private static final int TICKS_PER_SECOND = 20;
    private static final int SYNC_INTERVAL_TICKS = 20;

    private static final Map<UUID, List<TrackedEffect>> ACTIVE = new HashMap<>();
    private static int tickCounter = 0;

    private ActiveEffectsManager() {}

    public static void register(ServerPlayer player, BaseEvent event) {
        if (event.getDurationTicks() <= 0) return;
        UUID id = player.getUUID();
        long now = player.getServer() != null ? player.getServer().getTickCount() : 0;
        long endTick = now + event.getDurationTicks();
        TrackedEffect tracked = new TrackedEffect(
                event.getId(),
                event.getDisplayName(),
                event.getType().ordinal(),
                endTick);
        List<TrackedEffect> list = ACTIVE.computeIfAbsent(id, u -> new ArrayList<>());
        list.add(tracked);
        int max = Math.max(1, ConfigManager.get().maxActiveEffects);
        while (list.size() > max) {
            list.remove(0);
        }
        sendTo(player);
    }

    public static void clear(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
        sendTo(player);
    }

    public static void tick(MinecraftServer server) {
        long now = server.getTickCount();
        boolean anyChanged = false;

        Iterator<Map.Entry<UUID, List<TrackedEffect>>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, List<TrackedEffect>> e = it.next();
            List<TrackedEffect> list = e.getValue();
            boolean changed = list.removeIf(t -> now >= t.endTick);
            if (list.isEmpty()) {
                it.remove();
                anyChanged = true;
            } else if (changed) {
                anyChanged = true;
            }
        }

        tickCounter++;
        if (anyChanged || tickCounter % SYNC_INTERVAL_TICKS == 0) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                sendTo(p);
            }
        }
    }

    private static void sendTo(ServerPlayer player) {
        if (player.getServer() == null) return;
        long now = player.getServer().getTickCount();
        List<TrackedEffect> list = ACTIVE.get(player.getUUID());
        List<ActiveEffectsPacket.Entry> entries = new ArrayList<>();
        if (list != null) {
            for (TrackedEffect t : list) {
                int remainingTicks = (int) Math.max(0L, t.endTick - now);
                int seconds = (remainingTicks + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
                entries.add(new ActiveEffectsPacket.Entry(
                        t.displayName, t.typeOrdinal, seconds));
            }
        }
        ServerPlayNetworking.send(player, new ActiveEffectsPacket(entries));
    }

    private static final class TrackedEffect {
        final String eventId;
        final String displayName;
        final int typeOrdinal;
        final long endTick;

        TrackedEffect(String eventId, String displayName, int typeOrdinal, long endTick) {
            this.eventId = eventId;
            this.displayName = displayName;
            this.typeOrdinal = typeOrdinal;
            this.endTick = endTick;
        }
    }
}
