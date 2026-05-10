package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.network.NoJumpPacket;
import com.chaosroll.util.EventNotifyUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NoJumpEvent extends BaseEvent {
    @Override public String getId() { return "no_jump"; }
    @Override public String getDisplayName() { return "Без стрибків"; }
    @Override public String getDescription() { return "30с — стрибки повністю заблоковані (працює тільки якщо мод стоїть у клієнта)."; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        int duration = getDurationTicks();
        int endTick = context.server().getTickCount() + duration;
        CoopState.NO_JUMP.put(player.getUUID(), endTick);
        ServerPlayNetworking.send(player, new NoJumpPacket(duration));
        EventNotifyUtil.notifyPlayer(player, this, "30с — ти не можеш стрибати взагалі!");
    }
}