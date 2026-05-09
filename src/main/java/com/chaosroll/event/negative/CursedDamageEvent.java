package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class CursedDamageEvent extends BaseEvent {
    @Override public String getId() { return "cursed_damage"; }
    @Override public String getDisplayName() { return "Прокляття дамагу"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 16; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 600;
        CoopState.CURSED_DAMAGE.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this,
                "30с — твій дамаг ЛІКУЄ ворогів і ранить тебе!");
    }
}
