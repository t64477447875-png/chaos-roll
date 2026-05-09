package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatCircleEvent extends BaseEvent {
    @Override public String getId() { return "boat_circle"; }
    @Override public String getDisplayName() { return "Човновий вир"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 28; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8;
            double r = 3;
            Boat boat = EntityType.BOAT.create(context.world());
            if (boat == null) continue;
            boat.setPos(player.getX() + Math.cos(angle) * r, player.getY(), player.getZ() + Math.sin(angle) * r);
            context.world().addFreshEntity(boat);
        }
        EventNotifyUtil.notifyPlayer(player, this, "8 човнів навколо тебе");
    }
}