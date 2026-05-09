package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;

public class LoseXpEvent extends BaseEvent {
    @Override public String getId() { return "lose_xp"; }
    @Override public String getDisplayName() { return "Втрата досвіду"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int loss = Math.min(5, player.experienceLevel);
        if (loss > 0) {
            player.giveExperienceLevels(-loss);
        }
        EventNotifyUtil.notifyPlayer(player, this, "-" + loss + " рівнів XP");
    }
}