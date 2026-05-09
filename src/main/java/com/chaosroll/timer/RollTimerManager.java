package com.chaosroll.timer;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.network.TimerSyncPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RollTimerManager {

    private static final int TICKS_PER_SECOND = 20;

    public static int getRollIntervalSeconds() {
        return ConfigManager.get().rollIntervalSeconds;
    }

    private static final Map<UUID, PlayerTimer> TIMERS = new HashMap<>();

    private RollTimerManager() {}

    public static void onPlayerJoin(ServerPlayer player) {
        TIMERS.put(player.getUUID(), new PlayerTimer(getRollIntervalSeconds() * TICKS_PER_SECOND, false));
        sendSync(player);
    }

    public static void onPlayerLeave(ServerPlayer player) {
        TIMERS.remove(player.getUUID());
    }

    public static void tickPlayer(ServerPlayer player) {
        UUID id = player.getUUID();
        PlayerTimer t = TIMERS.computeIfAbsent(id,
                u -> new PlayerTimer(getRollIntervalSeconds() * TICKS_PER_SECOND, false));

        if (t.rollReady) {
            return;
        }

        int prevSeconds = secondsFromTicks(t.ticksLeft);
        t.ticksLeft--;

        if (t.ticksLeft <= 0) {
            t.ticksLeft = 0;
            t.rollReady = true;
            sendSync(player);
            return;
        }

        int currSeconds = secondsFromTicks(t.ticksLeft);
        if (currSeconds != prevSeconds) {
            sendSync(player);
        }
    }

    public static void resetPlayer(ServerPlayer player) {
        UUID id = player.getUUID();
        int intervalTicks = getRollIntervalSeconds() * TICKS_PER_SECOND;
        PlayerTimer t = TIMERS.computeIfAbsent(id,
                u -> new PlayerTimer(intervalTicks, false));
        t.ticksLeft = intervalTicks;
        t.rollReady = false;
        sendSync(player);
    }

    public static void markRolling(ServerPlayer player) {
        PlayerTimer t = TIMERS.get(player.getUUID());
        if (t == null) return;
        t.rollReady = false;
        sendSync(player);
    }

    public static boolean isRollReady(ServerPlayer player) {
        PlayerTimer t = TIMERS.get(player.getUUID());
        return t != null && t.rollReady;
    }

    private static void sendSync(ServerPlayer player) {
        PlayerTimer t = TIMERS.get(player.getUUID());
        if (t == null) return;
        int seconds = secondsFromTicks(t.ticksLeft);
        ServerPlayNetworking.send(player, new TimerSyncPacket(seconds, t.rollReady));
    }

    private static int secondsFromTicks(int ticks) {
        return (ticks + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
    }

    private static final class PlayerTimer {
        int ticksLeft;
        boolean rollReady;

        PlayerTimer(int ticksLeft, boolean rollReady) {
            this.ticksLeft = ticksLeft;
            this.rollReady = rollReady;
        }
    }
}