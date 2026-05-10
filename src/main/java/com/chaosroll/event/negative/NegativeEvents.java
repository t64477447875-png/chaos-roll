package com.chaosroll.event.negative;

import com.chaosroll.event.EventRegistry;

public final class NegativeEvents {

    private NegativeEvents() {}

    public static void registerAll() {
        EventRegistry.register(new LavaPitEvent());
        EventRegistry.register(new MobSwarmEvent());
        EventRegistry.register(new IceCageEvent());
        EventRegistry.register(new FallFromSkyEvent());
        EventRegistry.register(new DropInventoryEvent());
        EventRegistry.register(new HungerCurseEvent());
        EventRegistry.register(new BlindnessEvent());
        EventRegistry.register(new SlownessEvent());
        EventRegistry.register(new WeaknessEvent());
        EventRegistry.register(new MiningFatigueEvent());
        EventRegistry.register(new LevitationEvent());
        EventRegistry.register(new PoisonEvent());
        EventRegistry.register(new WitherEvent());
        EventRegistry.register(new LoseXpEvent());
        EventRegistry.register(new FogEvent());
        EventRegistry.register(new AnvilCurseEvent());
        EventRegistry.register(new LightningStrikeEvent());
        EventRegistry.register(new ItemCorruptionEvent());
        EventRegistry.register(new GravityFlipEvent());
        EventRegistry.register(new NoJumpEvent());
        EventRegistry.register(new BrittleBonesEvent());
        EventRegistry.register(new GlassFloorEvent());
        EventRegistry.register(new BrokenLegsEvent());
        EventRegistry.register(new MidnightCurseEvent());
        EventRegistry.register(new HallucinationEvent());
        EventRegistry.register(new DeathRollEvent());
        EventRegistry.register(new ExileTeleportEvent());
        EventRegistry.register(new FireballRainEvent());
        EventRegistry.register(new VindicatorAmbushEvent());
        EventRegistry.register(new CursedSpawnEvent());
        EventRegistry.register(new EarthquakeEvent());
        EventRegistry.register(new MirrorMatchEvent());
        EventRegistry.register(new PacifistEvent());
        EventRegistry.register(new CursedDamageEvent());
        EventRegistry.register(new TntBlockEvent());
        EventRegistry.register(new SandTombEvent());
        EventRegistry.register(new CobbleBoxEvent());
        EventRegistry.register(new DropTheMicEvent());
        EventRegistry.register(new BedrockCageEvent());
        EventRegistry.register(new IronLungEvent());
        EventRegistry.register(new BatStormEvent());
        EventRegistry.register(new MagnetCurseEvent());
        EventRegistry.register(new TotalDarknessEvent());
        EventRegistry.register(new SoulBurnEvent());
    }
}