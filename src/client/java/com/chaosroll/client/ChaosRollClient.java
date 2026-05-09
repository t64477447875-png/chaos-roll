package com.chaosroll.client;

import com.chaosroll.ChaosRollMod;
import com.chaosroll.client.hud.ChaosHudRenderer;
import net.fabricmc.api.ClientModInitializer;

public class ChaosRollClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ChaosRollMod.LOGGER.info("[Chaos Roll] Initializing client-side...");

        RollKeybind.register();
        ChaosHudRenderer.register();

        ChaosRollMod.LOGGER.info("[Chaos Roll] Client initialization complete.");
    }
}