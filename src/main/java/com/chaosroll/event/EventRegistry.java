package com.chaosroll.event;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.config.ChaosRollConfig;
import com.chaosroll.config.ConfigManager;
import com.chaosroll.data.ChaosRollSavedData;
import com.chaosroll.data.PlayerStats;
import com.chaosroll.util.WeightedRandom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class EventRegistry {

    private static final List<BaseEvent> ALL_EVENTS = new ArrayList<>();

    private EventRegistry() {}

    public static void register(BaseEvent event) {
        ALL_EVENTS.add(event);
    }

    public static List<BaseEvent> all() {
        return List.copyOf(ALL_EVENTS);
    }

    public static int size() {
        return ALL_EVENTS.size();
    }

    public static BaseEvent getById(String id) {
        if (id == null) return null;
        for (BaseEvent e : ALL_EVENTS) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public static int getNegativeStreak(UUID id, MinecraftServer server) {
        if (server == null) return 0;
        return ChaosRollSavedData.get(server).negativeStreak.getOrDefault(id, 0);
    }

    public static int getPositiveStreak(UUID id, MinecraftServer server) {
        if (server == null) return 0;
        return ChaosRollSavedData.get(server).positiveStreak.getOrDefault(id, 0);
    }

    public static void bootstrap() {
        Events.registerAll();
        ChaosRollMod.LOGGER.info("[Chaos Roll] Registered {} events.", ALL_EVENTS.size());
    }

    public static BaseEvent pickFor(ServerPlayer player) {
        if (ALL_EVENTS.isEmpty()) return null;
        EventContext ctx = EventContext.forPlayer(player);
        ChaosRollConfig cfg = ConfigManager.get();
        Set<String> disabled = cfg.disabledEventIds == null
                ? Set.of() : new HashSet<>(cfg.disabledEventIds);

        MinecraftServer server = player.getServer();
        ChaosRollSavedData data = server == null ? null : ChaosRollSavedData.get(server);
        UUID id = player.getUUID();
        int negStreak = data == null ? 0 : data.negativeStreak.getOrDefault(id, 0);
        int posStreak = data == null ? 0 : data.positiveStreak.getOrDefault(id, 0);
        int maxNegStreak = Math.max(0, cfg.guaranteePositiveAfterNegativeStreak);
        int maxPosStreak = Math.max(0, cfg.guaranteeNonPositiveAfterPositiveStreak);
        boolean forcePositive = maxNegStreak > 0 && negStreak >= maxNegStreak;
        boolean forceNonPositive = !forcePositive && maxPosStreak > 0 && posStreak >= maxPosStreak;

        Set<String> personalBlocklist = data == null ? Set.of()
                : new HashSet<>(data.getOrCreateStats(id).blocklist);

        List<BaseEvent> pool = new ArrayList<>();
        for (BaseEvent e : ALL_EVENTS) {
            if (disabled.contains(e.getId())) continue;
            if (personalBlocklist.contains(e.getId())) continue;
            if (!isTypeEnabled(cfg, e.getType())) continue;
            if (!cfg.globalEventsEnabled && e.isGlobal()) continue;
            if (!e.canExecute(ctx)) continue;
            if (forcePositive && e.getType() != EventType.POSITIVE) continue;
            if (forceNonPositive && e.getType() == EventType.POSITIVE) continue;
            pool.add(e);
        }

        if (pool.isEmpty() && (forcePositive || forceNonPositive)) {
            for (BaseEvent e : ALL_EVENTS) {
                if (disabled.contains(e.getId())) continue;
                if (personalBlocklist.contains(e.getId())) continue;
                if (!isTypeEnabled(cfg, e.getType())) continue;
                if (!cfg.globalEventsEnabled && e.isGlobal()) continue;
                if (!e.canExecute(ctx)) continue;
                pool.add(e);
            }
        }
        if (pool.isEmpty()) return null;

        BaseEvent picked = WeightedRandom.pick(pool, ctx.random(), player);
        if (picked == null) return null;

        if (data != null) {
            if (picked.getType() == EventType.NEGATIVE) {
                data.negativeStreak.merge(id, 1, Integer::sum);
                data.positiveStreak.put(id, 0);
            } else if (picked.getType() == EventType.POSITIVE) {
                data.positiveStreak.merge(id, 1, Integer::sum);
                data.negativeStreak.put(id, 0);
            } else {
                data.negativeStreak.put(id, 0);
                data.positiveStreak.put(id, 0);
            }
            PlayerStats stats = data.getOrCreateStats(id);
            stats.totalRolls++;
            stats.byEventId.merge(picked.getId(), 1, Integer::sum);
            switch (picked.getType()) {
                case POSITIVE -> stats.positiveCount++;
                case NEGATIVE -> stats.negativeCount++;
                case CHAOTIC  -> stats.chaoticCount++;
            }
            data.setDirty();
        }
        return picked;
    }

    private static boolean isTypeEnabled(ChaosRollConfig cfg, EventType type) {
        return switch (type) {
            case POSITIVE -> cfg.enablePositiveEvents;
            case NEGATIVE -> cfg.enableNegativeEvents;
            case CHAOTIC  -> cfg.enableChaoticEvents;
        };
    }
}
