package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class PathBuilderEvent extends BaseEvent {
    @Override public String getId() { return "path_builder"; }
    @Override public String getDisplayName() { return "Шлях під ногами"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 900; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 900;
        CoopState.PATH_BUILDER.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "45с — під ногами авто-спавн платформи (можна над безоднею).");
    }
}
