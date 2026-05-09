package com.chaosroll;

import com.chaosroll.event.EventRegistry;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.network.NetworkHandler;
import com.chaosroll.timer.RollTimerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaosRollMod implements ModInitializer {

    public static final String MOD_ID = "chaosroll";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Chaos Roll] Initializing mod (server-side + shared)...");

        EventRegistry.bootstrap();
        NetworkHandler.registerPayloads();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                RollTimerManager.onPlayerJoin(handler.getPlayer()));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                RollTimerManager.onPlayerLeave(handler.getPlayer()));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ScheduledTaskManager.tick(server);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                RollTimerManager.tickPlayer(player);
            }
        });

        LOGGER.info("[Chaos Roll] Mod initialization complete.");
    }
}