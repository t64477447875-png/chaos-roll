package com.chaosroll;

import com.chaosroll.command.ChaosRollCommand;
import com.chaosroll.config.ConfigManager;
import com.chaosroll.event.ActiveEffectsManager;
import com.chaosroll.event.EventRegistry;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.event.coop.CoopTickHandler;
import com.chaosroll.network.NetworkHandler;
import com.chaosroll.timer.RollTimerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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

        ConfigManager.init();
        EventRegistry.bootstrap();
        com.chaosroll.event.EventHooks.register();
        NetworkHandler.registerPayloads();
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, env) ->
                ChaosRollCommand.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            RollTimerManager.onPlayerJoin(handler.getPlayer());
            NetworkHandler.sendConfigTo(handler.getPlayer());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                RollTimerManager.onPlayerLeave(handler.getPlayer()));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ScheduledTaskManager.tick(server);
            CoopTickHandler.tick(server);
            ActiveEffectsManager.tick(server);
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                RollTimerManager.tickPlayer(player);
            }
            if (server.getTickCount() % 40 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    net.minecraft.network.chat.Component footer =
                            com.chaosroll.achievement.AchievementManager.buildTabFooter(player);
                    player.connection.send(new net.minecraft.network.protocol.game.ClientboundTabListPacket(
                            net.minecraft.network.chat.Component.empty(), footer));
                }
            }
        });

        LOGGER.info("[Chaos Roll] Mod initialization complete.");
    }
}