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

    public static final class LifelineSession {
        public final UUID a;
        public final UUID b;
        public final int endTick;

        public LifelineSession(UUID a, UUID b, int endTick) {
            this.a = a;
            this.b = b;
            this.endTick = endTick;
        }
    }

    public static final class SpeedrunSession {
        public final int targetX;
        public final int targetY;
        public final int targetZ;
        public final int endTick;
        public boolean ended;

        public SpeedrunSession(int x, int y, int z, int endTick) {
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
            this.endTick = endTick;
            this.ended = false;
        }
    }

    public static final class HungerGamesSession {
        public final int endTick;
        public boolean ended;

        public HungerGamesSession(int endTick) {
            this.endTick = endTick;
            this.ended = false;
        }
    }

    public static final Map<UUID, SharedHealthSession> SHARED_HEALTH = new HashMap<>();
    public static final Map<UUID, TwinFateSession> TWIN_FATE = new HashMap<>();
    public static final Map<UUID, HotPotatoSession> HOT_POTATO = new HashMap<>();
    public static final Map<UUID, LifelineSession> LIFELINE = new HashMap<>();
    public static SpeedrunSession SPEEDRUN = null;
    public static HungerGamesSession HUNGER_GAMES = null;
    public static final Map<UUID, Integer> BERSERKER = new HashMap<>();
    public static final Map<UUID, Integer> BLOCK_ROULETTE = new HashMap<>();
    public static final Map<UUID, Integer> CURSED_DAMAGE = new HashMap<>();
}
