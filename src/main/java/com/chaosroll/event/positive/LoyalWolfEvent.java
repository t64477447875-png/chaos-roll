package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Wolf;

public class LoyalWolfEvent extends BaseEvent {
    @Override public String getId() { return "loyal_wolf"; }
    @Override public String getDisplayName() { return "Вірний пес"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 30; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        Wolf wolf = EntityType.WOLF.create(context.world());
        if (wolf == null) return;
        wolf.setPos(player.getX() + 1, player.getY(), player.getZ() + 1);
        wolf.tame(player);
        wolf.setHealth(wolf.getMaxHealth());
        context.world().addFreshEntity(wolf);
        EventNotifyUtil.notifyPlayer(player, this, "Вовк приручений");
    }
}