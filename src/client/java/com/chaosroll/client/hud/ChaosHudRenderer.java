package com.chaosroll.client.hud;

import com.chaosroll.network.ActiveEffectsPacket;
import com.chaosroll.network.GlobalEventPacket;
import com.chaosroll.network.RollResultPacket;
import com.chaosroll.network.TimerSyncPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class ChaosHudRenderer {

    private ChaosHudRenderer() {}

    public static void register() {
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            TimerBarRenderer.render(ctx);
            ActiveEffectsRenderer.render(ctx);
            GlobalBannerRenderer.render(ctx);
            RollButtonRenderer.render(ctx);
            RollAnimationRenderer.render(ctx);
        });

        ClientPlayNetworking.registerGlobalReceiver(TimerSyncPacket.TYPE, (payload, context) -> {
            TimerState.update(payload.secondsRemaining(), payload.rollReady());
        });

        ClientPlayNetworking.registerGlobalReceiver(RollResultPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
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

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused()) return;
            RollAnimationState.clientTick();
            GlobalBannerState.clientTick();
        });
    }
}
