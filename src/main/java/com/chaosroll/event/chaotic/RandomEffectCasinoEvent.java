package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.util.EventNotifyUtil;

public class RandomEffectCasinoEvent extends BaseEvent {
    @Override public String getId() { return "effect_casino"; }
    @Override public String getDisplayName() { return "Казино ефектів"; }
    @Override public String getDescription() { return "60с — кожні 5с випадковий ефект (плюс або мінус)"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int endTick = context.server().getTickCount() + 1200;
        CoopState.EFFECT_CASINO.put(player.getUUID(), endTick);
        EventNotifyUtil.notifyPlayer(player, this, "Казино ефектів 60с — рулетка щосекунд!");
    }
}
