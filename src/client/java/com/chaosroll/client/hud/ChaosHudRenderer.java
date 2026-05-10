package com.chaosroll.client.hud;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.client.state.ScreenFlipState;
import com.chaosroll.network.ActiveEffectsPacket;
import com.chaosroll.network.GlobalEventPacket;
import com.chaosroll.network.RollResultPacket;
import com.chaosroll.network.ScreenFlipPacket;
import com.chaosroll.network.TimerSyncPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class ChaosHudRenderer {

    private ChaosHudRenderer() {}

    public static void register() {
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            var cfg = ConfigManager.get();
            if (cfg.showTimerBar) TimerBarRenderer.render(ctx);
            if (cfg.showActiveEffectsPanel) ActiveEffectsRenderer.render(ctx);
            if (cfg.globalEventsEnabled) GlobalBannerRenderer.render(ctx);
            if (cfg.showTimerBar) RollButtonRenderer.render(ctx);
            if (cfg.enableRollAnimation) RollAnimationRenderer.render(ctx);
        });

        ClientPlayNetworking.registerGlobalReceiver(TimerSyncPacket.TYPE, (payload, context) -> {
            TimerState.update(payload.secondsRemaining(), payload.rollReady());
        });

        ClientPlayNetworking.registerGlobalReceiver(RollResultPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (!ConfigManager.get().enableRollAnimation) return;
                RollAnimationState.registerSpinName(payload.displayName());
                RollAnimationState.start(
                        payload.eventId(),
                        payload.displayName(),
                        payload.typeOrdinal(),
                        payload.rarityOrdinal()
                );
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(GlobalEventPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player == null) return;
                String localName = context.client().player.getName().getString();
                if (localName.equals(payload.initiatorName())) return;
                GlobalBannerState.show(
                        payload.initiatorName(),
                        payload.displayName(),
                        payload.typeOrdinal());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ActiveEffectsPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> ActiveEffectsState.update(payload.entries()));
        });

        ClientPlayNetworking.registerGlobalReceiver(ScreenFlipPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> ScreenFlipState.start(payload.durationTicks()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused()) return;
            RollAnimationState.clientTick();
            GlobalBannerState.clientTick();
        });
    }
}
