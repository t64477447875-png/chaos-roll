package com.chaosroll.event;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.config.ChaosRollConfig;
import com.chaosroll.config.ConfigManager;
import com.chaosroll.util.WeightedRandom;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class EventRegistry {

    private static final List<BaseEvent> ALL_EVENTS = new ArrayList<>();
    private static final Map<UUID, Integer> NEGATIVE_STREAK = new HashMap<>();
    private static final Map<UUID, Integer> POSITIVE_STREAK = new HashMap<>();

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

    public static int getNegativeStreak(UUID id) {
        return NEGATIVE_STREAK.getOrDefault(id, 0);
    }

    public static int getPositiveStreak(UUID id) {
        return POSITIVE_STREAK.getOrDefault(id, 0);
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

        UUID id = player.getUUID();
        int negStreak = NEGATIVE_STREAK.getOrDefault(id, 0);
        int posStreak = POSITIVE_STREAK.getOrDefault(id, 0);
        int maxNegStreak = Math.max(0, cfg.guaranteePositiveAfterNegativeStreak);
        int maxPosStreak = Math.max(0, cfg.guaranteeNonPositiveAfterPositiveStreak);
        boolean forcePositive = maxNegStreak > 0 && negStreak >= maxNegStreak;
        boolean forceNonPositive = !forcePositive && maxPosStreak > 0 && posStreak >= maxPosStreak;

        List<BaseEvent> pool = new ArrayList<>();
        for (BaseEvent e : ALL_EVENTS) {
            if (disabled.contains(e.getId())) continue;
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
                if (!isTypeEnabled(cfg, e.getType())) continue;
                if (!cfg.globalEventsEnabled && e.isGlobal()) continue;
                if (!e.canExecute(ctx)) continue;
                pool.add(e);
            }
        }
        if (pool.isEmpty()) return null;

        BaseEvent picked = WeightedRandom.pick(pool, ctx.random());
        if (picked == null) return null;

        if (picked.getType() == EventType.NEGATIVE) {
            NEGATIVE_STREAK.merge(id, 1, Integer::sum);
            POSITIVE_STREAK.put(id, 0);
        } else if (picked.getType() == EventType.POSITIVE) {
            POSITIVE_STREAK.merge(id, 1, Integer::sum);
            NEGATIVE_STREAK.put(id, 0);
        } else {
            NEGATIVE_STREAK.put(id, 0);
            POSITIVE_STREAK.put(id, 0);
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
