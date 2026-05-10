package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class StaticShockEvent extends BaseEvent {
    @Override public String getId() { return "static_shock"; }
    @Override public String getDisplayName() { return "Електрика"; }
    @Override public String getDescription() { return "60с — кожні 5с по тобі б'є власна блискавка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 1200;
        CoopState.STATIC_SHOCK.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "60с — блискавки кожні 5с!");
    }
}
