package com.chaosroll.event.coop;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Central registry of active co-op event sessions.
 * A single per-tick handler ({@link CoopTickHandler}) walks these maps.
 */
public final class CoopState {

    private CoopState() {}

    public static final class SharedHealthSession {
        public final UUID a;
        public final UUID b;
        public final int endTick;
        public float lastHpA;
        public float lastHpB;

        public SharedHealthSession(UUID a, UUID b, int endTick, float hpA, float hpB) {
            this.a = a;
            this.b = b;
            this.endTick = endTick;
            this.lastHpA = hpA;
            this.lastHpB = hpB;
        }
    }

    public static final class TwinFateSession {
        public final UUID a;
        public final UUID b;
        public final int endTick;

        public TwinFateSession(UUID a, UUID b, int endTick) {
            this.a = a;
            this.b = b;
            this.endTick = endTick;
        }
    }

    public static final class HotPotatoSession {
        public final UUID owner;
        public final int endTick;
        public final ItemStack markerStack;

        public HotPotatoSession(UUID owner, int endTick, ItemStack markerStack) {
            this.owner = owner;
            this.endTick = endTick;
            this.markerStack = markerStack;
        }
    }

    public static final Map<UUID, SharedHealthSession> SHARED_HEALTH = new HashMap<>();
    public static final Map<UUID, TwinFateSession> TWIN_FATE = new HashMap<>();
    public static final Map<UUID, HotPotatoSession> HOT_POTATO = new HashMap<>();
}
