package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class GentleFlightEvent extends BaseEvent {
    @Override public String getId() { return "gentle_flight"; }
    @Override public String getDisplayName() { return "Тимчасовий політ"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 300; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        boolean wasFly = player.getAbilities().mayfly;
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();

        UUID id = player.getUUID();
        MinecraftServer server = context.server();
        com.chaosroll.event.ScheduledTaskManager.schedule(server, getDurationTicks(), srv -> {
            ServerPlayer p = srv.getPlayerList().getPlayer(id);
            if (p == null) return;
            if (!wasFly) {
                p.getAbilities().mayfly = false;
                p.getAbilities().flying = false;
                p.onUpdateAbilities();
            }
        });
        EventNotifyUtil.notifyPlayer(player, this, "15 сек політу (натисни 2x пробіл)");
    }
}