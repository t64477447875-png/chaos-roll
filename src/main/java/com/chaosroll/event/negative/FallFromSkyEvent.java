package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;

public class FallFromSkyEvent extends BaseEvent {
    @Override public String getId() { return "fall_from_sky"; }
    @Override public String getDisplayName() { return "Падіння з неба"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.teleportTo(player.getX(), 280, player.getZ());
        player.fallDistance = 0f;
        EventNotifyUtil.notifyPlayer(player, this, "Y=280 — без парашута. Шукай воду або еліткру");
    }
}
