package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;

public class DropTheMicEvent extends BaseEvent {
    @Override public String getId() { return "drop_the_mic"; }
    @Override public String getDisplayName() { return "Drop the Mic"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        double x = player.getX();
        double z = player.getZ();
        double targetY = Math.min(player.serverLevel().getMaxBuildHeight() - 5, 300);
        player.teleportTo(x, targetY, z);
        EventNotifyUtil.notifyPlayer(player, this, "Y=" + (int) targetY + ". Без парашута. Прощавай.");
    }
}
