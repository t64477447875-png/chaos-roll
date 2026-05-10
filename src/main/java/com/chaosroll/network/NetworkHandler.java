package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.config.ConfigManager;
import com.chaosroll.event.ActiveEffectsManager;
import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRegistry;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.timer.RollTimerManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class NetworkHandler {

    public static final int ANIMATION_DURATION_TICKS = 65;

    private NetworkHandler() {}

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(TimerSyncPacket.TYPE, TimerSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(RollResultPacket.TYPE, RollResultPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(GlobalEventPacket.TYPE, GlobalEventPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ActiveEffectsPacket.TYPE, ActiveEffectsPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ScreenFlipPacket.TYPE, ScreenFlipPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RollRequestPacket.TYPE, RollRequestPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RollRequestPacket.TYPE, (payload, context) -> {
            context.player().getServer().execute(() -> handleRollRequest(context.player()));
        });
    }

    private static void broadcastGlobalEvent(ServerPlayer initiator, BaseEvent event) {
        if (initiator.getServer() == null) return;
        if (!ConfigManager.get().globalEventsEnabled) return;
        GlobalEventPacket packet = new GlobalEventPacket(
                initiator.getName().getString(),
                event.getId(),
                event.getDisplayName(),
                event.getType().ordinal(),
                event.getRarity().ordinal());
        for (ServerPlayer p : initiator.getServer().getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(p, packet);
        }
    }

    public static boolean forceRoll(ServerPlayer player, boolean withAnimation) {
        BaseEvent event = EventRegistry.pickFor(player);
        if (event == null) return false;
        runEvent(player, event, withAnimation);
        return true;
    }

    public static boolean forceEvent(ServerPlayer player, BaseEvent event, boolean withAnimation) {
        if (event == null) return false;
        runEvent(player, event, withAnimation);
        return true;
    }

    private static void runEvent(ServerPlayer player, BaseEvent event, boolean withAnimation) {
        MinecraftServer server = player.getServer();
        RollTimerManager.markRolling(player);

        if (!withAnimation || server == null) {
            executeEvent(event, player);
            RollTimerManager.resetPlayer(player);
            if (server != null) {
                registerActiveEffect(server, player, event);
                if (event.isGlobal()) broadcastGlobalEvent(player, event);
            }
            return;
        }

        ServerPlayNetworking.send(player, new RollResultPacket(
                event.getId(),
                event.getDisplayName(),
                event.getType().ordinal(),
                event.getRarity().ordinal()
        ));

        if (ConfigManager.get().enableParticles) spawnResultParticles(player, event);
        if (ConfigManager.get().enableSounds) playResultSound(player, event);

        ScheduledTaskManager.schedule(server, ANIMATION_DURATION_TICKS, srv -> {
            ServerPlayer current = srv.getPlayerList().getPlayer(player.getUUID());
            if (current == null) return;
            executeEvent(event, current);
            RollTimerManager.resetPlayer(current);
            registerActiveEffect(srv, current, event);
            if (event.isGlobal()) {
                broadcastGlobalEvent(current, event);
            }
        });
    }

    private static void handleRollRequest(ServerPlayer player) {
        if (!RollTimerManager.isRollReady(player)) {
            return;
        }
        if (RollTimerManager.isPaused(player.getUUID())) {
            return;
        }
        BaseEvent event = EventRegistry.pickFor(player);
        if (event == null) {
            ChaosRollMod.LOGGER.info("[Chaos Roll] No events available - skipping roll for {}", player.getName().getString());
            RollTimerManager.resetPlayer(player);
            return;
        }

        RollTimerManager.markRolling(player);

        ServerPlayNetworking.send(player, new RollResultPacket(
                event.getId(),
                event.getDisplayName(),
                event.getType().ordinal(),
                event.getRarity().ordinal()
        ));

        ChaosRollMod.LOGGER.info("[Chaos Roll] Rolled '{}' for {} - playing animation, executing in {} ticks.",
                event.getId(), player.getName().getString(), ANIMATION_DURATION_TICKS);

        MinecraftServer server = player.getServer();
        if (server == null) {
            executeEvent(event, player);
            RollTimerManager.resetPlayer(player);
            return;
        }

        if (ConfigManager.get().enableParticles) spawnResultParticles(player, event);
        if (ConfigManager.get().enableSounds) playResultSound(player, event);

        ScheduledTaskManager.schedule(server, ANIMATION_DURATION_TICKS, srv -> {
            ServerPlayer current = srv.getPlayerList().getPlayer(player.getUUID());
            if (current == null) return;
            executeEvent(event, current);
            RollTimerManager.resetPlayer(current);
            registerActiveEffect(srv, current, event);
            if (event.isGlobal()) {
                broadcastGlobalEvent(current, event);
            }
        });
    }

    private static void registerActiveEffect(MinecraftServer server, ServerPlayer initiator, BaseEvent event) {
        if (event.getDurationTicks() <= 0) return;
        if (event.isGlobal()) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                ActiveEffectsManager.register(p, event);
            }
        } else {
            ActiveEffectsManager.register(initiator, event);
        }
    }

    private static void executeEvent(BaseEvent event, ServerPlayer player) {
        EventContext ctx = EventContext.forPlayer(player);
        try {
            ChaosRollMod.LOGGER.info("[Chaos Roll] Executing event '{}' for {}", event.getId(), player.getName().getString());
            event.execute(ctx);
        } catch (Throwable err) {
            ChaosRollMod.LOGGER.warn("[Chaos Roll] Event '{}' failed: {}", event.getId(), err.getMessage());
        }
    }

    private static void spawnResultParticles(ServerPlayer player, BaseEvent event) {
        ServerLevel world = player.serverLevel();
        ParticleOptions particle = switch (event.getType()) {
            case POSITIVE -> ParticleTypes.HAPPY_VILLAGER;
            case NEGATIVE -> ParticleTypes.SMOKE;
            case CHAOTIC -> ParticleTypes.FIREWORK;
        };
        world.sendParticles(particle,
                player.getX(), player.getY() + 1.0, player.getZ(),
                40, 0.8, 1.0, 0.8, 0.05);

        if (event.getType() == com.chaosroll.event.EventType.POSITIVE) {
            world.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1.5, player.getZ(),
                    20, 0.6, 0.8, 0.6, 0.02);
        }
    }

    private static void playResultSound(ServerPlayer player, BaseEvent event) {
        ServerLevel world = player.serverLevel();
        SoundEvent sound = switch (event.getType()) {
            case POSITIVE -> SoundEvents.PLAYER_LEVELUP;
            case NEGATIVE -> SoundEvents.WARDEN_HEARTBEAT;
            case CHAOTIC -> SoundEvents.FIREWORK_ROCKET_BLAST;
        };
        float pitch = event.getType() == com.chaosroll.event.EventType.NEGATIVE ? 0.7f : 1.0f;
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                sound, SoundSource.PLAYERS, 0.8f, pitch);
    }
}
