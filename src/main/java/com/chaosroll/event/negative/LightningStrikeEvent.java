package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class LightningStrikeEvent extends BaseEvent {
    @Override public String getId() { return "lightning_strike"; }
    @Override public String getDisplayName() { return "Удар блискавки"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setHealth(player.getMaxHealth());
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(context.world());
        if (bolt == null) return;
        bolt.moveTo(player.getX(), player.getY(), player.getZ());
        context.world().addFreshEntity(bolt);
        EventNotifyUtil.notifyPlayer(player, this, "БУМ!");
    }
}