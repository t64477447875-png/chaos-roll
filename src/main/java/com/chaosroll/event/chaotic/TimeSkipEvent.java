package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;

public class TimeSkipEvent extends BaseEvent {
    @Override public String getId() { return "time_skip"; }
    @Override public String getDisplayName() { return "Стрибок часу"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        long current = context.world().getDayTime();
        long jump = context.random().nextBoolean() ? 6000 : 12000;
        context.world().setDayTime(current + jump);
        EventNotifyUtil.notifyAll(context.player(), this, "Час стрибнув вперед на " + (jump / 1000) + " годин");
    }
}