package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class LootRouletteEvent extends BaseEvent {
    @Override public String getId() { return "loot_roulette"; }
    @Override public String getDisplayName() { return "Лут-рулетка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 1200;
        CoopState.RANDOM_LOOT.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "60с — кожен блок/моб дає випадковий предмет!");
    }
}
