package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class MagnetCurseEvent extends BaseEvent {
    @Override public String getId() { return "magnet_curse"; }
    @Override public String getDisplayName() { return "Магніт-прокляття"; }
    @Override public String getDescription() { return "30с — кожні 5с предмети з рук вилітають"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 9; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 600;
        CoopState.MAGNET_CURSE.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "Магніт-прокляття 30с — лови предмети!");
    }
}
