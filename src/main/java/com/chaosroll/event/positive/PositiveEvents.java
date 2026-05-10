package com.chaosroll.event.positive;

import com.chaosroll.event.EventRegistry;

public final class PositiveEvents {

    private PositiveEvents() {}

    public static void registerAll() {
        EventRegistry.register(new RandomLootEvent());
        EventRegistry.register(new FullDiamondArmorEvent());
        EventRegistry.register(new ElytraGiftEvent());
        EventRegistry.register(new GentleFlightEvent());
        EventRegistry.register(new SpeedDemonEvent());
        EventRegistry.register(new ShadowModeEvent());
        EventRegistry.register(new LuckyTeleportEvent());
        EventRegistry.register(new LoyalWolfEvent());
        EventRegistry.register(new GoldenFeastEvent());
        EventRegistry.register(new FullRestoreEvent());
        EventRegistry.register(new TotemPackEvent());
        EventRegistry.register(new EnchantedToolsEvent());
        EventRegistry.register(new MysteryTraderEvent());
        EventRegistry.register(new ShulkerSurpriseEvent());
        EventRegistry.register(new GodModeEvent());
        EventRegistry.register(new TreasureMapEvent());
        EventRegistry.register(new ServerHealthBoostEvent());
        EventRegistry.register(new MaxHorseEvent());
        EventRegistry.register(new EnderStashEvent());
        EventRegistry.register(new RandomEnchantEvent());
        EventRegistry.register(new PowerSurgeEvent());
        EventRegistry.register(new MiningFrenzyEvent());
        EventRegistry.register(new TreasureBurstEvent());
        EventRegistry.register(new PhoenixRiseEvent());
        EventRegistry.register(new MidasTouchEvent());
        EventRegistry.register(new GuardianAngelEvent());
        EventRegistry.register(new HomeBeaconEvent());
        EventRegistry.register(new AutoMineEvent());
        EventRegistry.register(new EmeraldShowerEvent());
    }
}