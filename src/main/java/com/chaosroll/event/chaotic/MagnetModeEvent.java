package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public class MagnetModeEvent extends BaseEvent {
    @Override public String getId() { return "magnet_mode"; }
    @Override public String getDisplayName() { return "Магніт"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var items = context.world().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(30));
        for (ItemEntity ie : items) {
            Vec3 dir = player.position().subtract(ie.position()).normalize().scale(0.6);
            ie.setDeltaMovement(dir);
            ie.hasImpulse = true;
        }
        EventNotifyUtil.notifyPlayer(player, this, "Притягнено " + items.size() + " предметів");
    }
}