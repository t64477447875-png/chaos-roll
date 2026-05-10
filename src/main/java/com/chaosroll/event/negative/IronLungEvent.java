package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class IronLungEvent extends BaseEvent {
    @Override public String getId() { return "iron_lung"; }
    @Override public String getDisplayName() { return "Залізні легені"; }
    @Override public String getDescription() { return "60с задихаєшся ніби під водою на суші"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 1200;
        CoopState.IRON_LUNG.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "60с — задихаєшся поза водою!");
    }
}
