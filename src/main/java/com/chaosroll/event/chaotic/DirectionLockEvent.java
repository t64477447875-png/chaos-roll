package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class DirectionLockEvent extends BaseEvent {
    @Override public String getId() { return "direction_lock"; }
    @Override public String getDisplayName() { return "Блокування камери"; }
    @Override public String getDescription() { return "30с — камера зафіксована (yaw)"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 600;
        CoopState.DIRECTION_LOCK.put(player.getUUID(),
                new CoopState.DirectionLock(endTick, player.getYRot()));
        EventNotifyUtil.notifyPlayer(player, this, "30с — yaw зафіксовано на " + (int) player.getYRot() + "°!");
    }
}
