package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;

public class LuckyTeleportEvent extends BaseEvent {
    @Override public String getId() { return "lucky_teleport"; }
    @Override public String getDisplayName() { return "Щасливий телепорт"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 30; }

    @Override
    public void execute(EventContext context) {
        boolean ok = SafeTeleportUtil.teleportRandom(context.player(), 100);
        EventNotifyUtil.notifyPlayer(context.player(), this, ok ? "Телепортовано в безпечне місце" : "Не знайдено безпечного місця");
    }
}