package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class LifelineEvent extends BaseEvent {

    @Override public String getId() { return "coop_lifeline"; }
    @Override public String getDisplayName() { return "Lifeline / Прив'язка душ"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 12; }
    @Override public boolean isGlobal() { return true; }
    @Override public int getDurationTicks() { return 1800; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> others = CoopUtil.otherPlayers(initiator);
        if (others.isEmpty()) return;
        ServerPlayer partner = others.get(context.random().nextInt(others.size()));

        int endTick = context.server().getTickCount() + 1800;
        CoopState.LifelineSession session = new CoopState.LifelineSession(
                initiator.getUUID(), partner.getUUID(), endTick);
        CoopState.LIFELINE.put(initiator.getUUID(), session);
        CoopState.LIFELINE.put(partner.getUUID(), session);

        EventNotifyUtil.notifyAll(initiator, this,
                initiator.getName().getString() + " і " + partner.getName().getString()
                        + " прив'язані душею на 90с — один помре, другий теж!");
    }
}
