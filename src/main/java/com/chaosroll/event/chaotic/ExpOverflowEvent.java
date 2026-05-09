package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;

public class ExpOverflowEvent extends BaseEvent {
    @Override public String getId() { return "exp_overflow"; }
    @Override public String getDisplayName() { return "Потоп досвіду"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        context.player().giveExperienceLevels(10);
        EventNotifyUtil.notifyPlayer(context.player(), this, "+10 рівнів XP");
    }
}