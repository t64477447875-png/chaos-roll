package com.chaosroll.event.coop;

import com.chaosroll.event.EventRegistry;

public final class CoopEvents {

    private CoopEvents() {}

    public static void registerAll() {
        EventRegistry.register(new TreasureRaceEvent());
        EventRegistry.register(new SharedHealthEvent());
        EventRegistry.register(new MarksmanEvent());
        EventRegistry.register(new WolfPackEvent());
        EventRegistry.register(new HotPotatoEvent());
        EventRegistry.register(new InventorySwapEvent());
        EventRegistry.register(new TwinFateEvent());
        EventRegistry.register(new PortalChaosEvent());
        EventRegistry.register(new ArenaDuelEvent());
        EventRegistry.register(new CoopBossEvent());
        EventRegistry.register(new StructureTeleportEvent());
        EventRegistry.register(new SpeedrunEvent());
        EventRegistry.register(new LifelineEvent());
        EventRegistry.register(new HungerGamesEvent());
        EventRegistry.register(new VoteEvent());
        EventRegistry.register(new ItemTradeEvent());
    }
}
