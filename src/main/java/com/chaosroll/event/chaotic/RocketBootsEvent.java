package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class RocketBootsEvent extends BaseEvent {
    @Override public String getId() { return "rocket_boots"; }
    @Override public String getDisplayName() { return "Ракетні чоботи"; }
    @Override public String getDescription() { return "30с — стрибок викидає на 30 блоків угору"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 600;
        CoopState.ROCKET_BOOTS.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "30с — стрибок викидає тебе на 30 блоків!");
    }
}
