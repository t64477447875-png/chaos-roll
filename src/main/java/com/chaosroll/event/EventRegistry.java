package com.chaosroll.event;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.util.WeightedRandom;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EventRegistry {

    private static final List<BaseEvent> ALL_EVENTS = new ArrayList<>();
    private static final Map<UUID, Integer> NEGATIVE_STREAK = new HashMap<>();
    private static final int MAX_NEGATIVE_STREAK = 3;

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

    public static void bootstrap() {
        Events.registerAll();
        ChaosRollMod.LOGGER.info("[Chaos Roll] Registered {} events.", ALL_EVENTS.size());
    }

    public static BaseEvent pickFor(ServerPlayer player) {
        if (ALL_EVENTS.isEmpty()) return null;
        EventContext ctx = EventContext.forPlayer(player);

        UUID id = player.getUUID();
        int streak = NEGATIVE_STREAK.getOrDefault(id, 0);

        List<BaseEvent> pool;
        if (streak >= MAX_NEGATIVE_STREAK) {
            pool = ALL_EVENTS.stream()
                    .filter(e -> e.getType() == EventType.POSITIVE && e.canExecute(ctx))
                    .toList();
            if (pool.isEmpty()) {
                pool = ALL_EVENTS.stream().filter(e -> e.canExecute(ctx)).toList();
            }
        } else {
            pool = ALL_EVENTS.stream().filter(e -> e.canExecute(ctx)).toList();
        }

        if (pool.isEmpty()) return null;

        BaseEvent picked = WeightedRandom.pick(pool, ctx.random());
        if (picked == null) return null;

        if (picked.getType() == EventType.NEGATIVE) {
            NEGATIVE_STREAK.merge(id, 1, Integer::sum);
        } else {
            NEGATIVE_STREAK.put(id, 0);
        }
        return picked;
    }
}