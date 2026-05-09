package com.chaosroll.event.chaotic;

import com.chaosroll.event.EventRegistry;

public final class ChaoticEvents {

    private ChaoticEvents() {}

    public static void registerAll() {
        EventRegistry.register(new RandomBlockSwapEvent());
        EventRegistry.register(new TimeSkipEvent());
        EventRegistry.register(new WeatherChaosEvent());
        EventRegistry.register(new MobMorphEvent());
        EventRegistry.register(new ItemRainEvent());
        EventRegistry.register(new SwapPositionEvent());
        EventRegistry.register(new DimensionSwapEvent());
        EventRegistry.register(new BiomeBlastEvent());
        EventRegistry.register(new CreeperPartyEvent());
        EventRegistry.register(new SheepInvasionEvent());
        EventRegistry.register(new SlotMachineEvent());
        EventRegistry.register(new ExpOverflowEvent());
        EventRegistry.register(new GravityInverseEvent());
        EventRegistry.register(new SuperJumpEvent());
        EventRegistry.register(new FastPickaxeEvent());
        EventRegistry.register(new MagnetModeEvent());
        EventRegistry.register(new DoubleDropsEvent());
        EventRegistry.register(new GlassArmorEvent());
        EventRegistry.register(new MirrorImageEvent());
        EventRegistry.register(new TreasureBlockEvent());
        EventRegistry.register(new RandomPotionEvent());
        EventRegistry.register(new SoundChaosEvent());
        EventRegistry.register(new ColorWorldEvent());
        EventRegistry.register(new InfiniteWaterEvent());
        EventRegistry.register(new MysteryBoxEvent());
        EventRegistry.register(new ChunkDeleteEvent());
        EventRegistry.register(new TntRainEvent());
        EventRegistry.register(new BoatCircleEvent());
        EventRegistry.register(new TallPlayerEvent());
        EventRegistry.register(new VillagerSwarmEvent());
        EventRegistry.register(new CowExplosionEvent());
        EventRegistry.register(new InventoryShuffleEvent());
        EventRegistry.register(new PiglinDealEvent());
        EventRegistry.register(new RouletteTeleportEvent());
        EventRegistry.register(new MobMultiplyEvent());
        EventRegistry.register(new BerserkerEvent());
        EventRegistry.register(new BlockRouletteEvent());
    }
}