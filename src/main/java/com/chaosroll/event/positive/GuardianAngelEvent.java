package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class GuardianAngelEvent extends BaseEvent {
    @Override public String getId() { return "guardian_angel"; }
    @Override public String getDisplayName() { return "Янгол-охоронець"; }
    @Override public String getDescription() { return "Якщо вмреш — респаун з повним HP/їжею. 1 раз."; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        CoopState.GUARDIAN_ANGEL.add(player.getUUID());
        EventNotifyUtil.notifyPlayer(player, this, "Янгол з тобою — раз вмреш, повернешся з усім!");
    }
}
