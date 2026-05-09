package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRegistry;
import com.chaosroll.timer.RollTimerManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class NetworkHandler {

    private NetworkHandler() {}

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(TimerSyncPacket.TYPE, TimerSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RollRequestPacket.TYPE, RollRequestPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RollRequestPacket.TYPE, (payload, context) -> {
            context.player().getServer().execute(() -> handleRollRequest(context.player()));
        });
    }

    private static void handleRollRequest(net.minecraft.server.level.ServerPlayer player) {
        if (!RollTimerManager.isRollReady(player)) {
            return;
        }
        BaseEvent event = EventRegistry.pickFor(player);
        if (event == null) {
            ChaosRollMod.LOGGER.info("[Chaos Roll] No events available - skipping roll for {}", player.getName().getString());
            RollTimerManager.resetPlayer(player);
            return;
        }
        EventContext ctx = EventContext.forPlayer(player);
        try {
            ChaosRollMod.LOGGER.info("[Chaos Roll] Executing event '{}' for {}", event.getId(), player.getName().getString());
            event.execute(ctx);
        } catch (Throwable err) {
            ChaosRollMod.LOGGER.warn("[Chaos Roll] Event '{}' failed: {}", event.getId(), err.getMessage());
        }
        RollTimerManager.resetPlayer(player);
    }
}