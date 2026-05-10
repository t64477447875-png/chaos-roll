package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class MidasTouchEvent extends BaseEvent {
    @Override public String getId() { return "midas_touch"; }
    @Override public String getDisplayName() { return "Дотик Мідаса"; }
    @Override public String getDescription() { return "30с — все що ламаєш дропає золото. Потім -10 HP."; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 600;
        CoopState.MIDAS_TOUCH.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "30с — золото з кожного блока! (потім -10 HP)");
    }
}
