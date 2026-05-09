package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class SwapPositionEvent extends BaseEvent {
    @Override public String getId() { return "swap_position"; }
    @Override public String getDisplayName() { return "Обмін місцями"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        List<ServerPlayer> others = context.server().getPlayerList().getPlayers().stream()
                .filter(p -> !p.getUUID().equals(player.getUUID())).toList();
        if (others.isEmpty()) {
            SafeTeleportUtil.teleportRandom(player, 100);
            EventNotifyUtil.notifyPlayer(player, this, "Нема з ким мінятись — телепорт");
            return;
        }
        ServerPlayer other = others.get(context.random().nextInt(others.size()));
        double x1 = player.getX(), y1 = player.getY(), z1 = player.getZ();
        player.teleportTo(other.getX(), other.getY(), other.getZ());
        other.teleportTo(x1, y1, z1);
        EventNotifyUtil.notifyAll(player, this, "Обмін з " + other.getName().getString());
    }
}