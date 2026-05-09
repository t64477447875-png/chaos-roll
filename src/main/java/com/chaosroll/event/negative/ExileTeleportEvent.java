package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;

public class ExileTeleportEvent extends BaseEvent {
    @Override public String getId() { return "exile_teleport"; }
    @Override public String getDisplayName() { return "Вигнання"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 25; }

    @Override
    public void execute(EventContext context) {
        boolean ok = SafeTeleportUtil.teleportRandom(context.player(), 500);
        EventNotifyUtil.notifyPlayer(context.player(), this,
                ok ? "Вигнаний на 500 блоків від бази!" : "Не знайшов безпечне місце");
    }
}
