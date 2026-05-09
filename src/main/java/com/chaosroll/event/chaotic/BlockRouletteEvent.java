package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class BlockRouletteEvent extends BaseEvent {
    @Override public String getId() { return "block_roulette"; }
    @Override public String getDisplayName() { return "Блок-рулетка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 22; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 1200;
        CoopState.BLOCK_ROULETTE.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this,
                "60с — кожен блок який ламаєш дропає випадковий інший блок!");
    }
}
