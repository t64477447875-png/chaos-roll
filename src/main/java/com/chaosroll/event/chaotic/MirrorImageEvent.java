package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;

public class MirrorImageEvent extends BaseEvent {
    @Override public String getId() { return "mirror_image"; }
    @Override public String getDisplayName() { return "Дзеркальне відображення"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        WanderingTrader t = EntityType.WANDERING_TRADER.create(context.world());
        if (t == null) return;
        t.setPos(player.getX() + 1, player.getY(), player.getZ() + 1);
        t.setCustomName(Component.literal("Mirror of " + player.getName().getString()));
        t.setCustomNameVisible(true);
        context.world().addFreshEntity(t);
        EventNotifyUtil.notifyPlayer(player, this, "Дзеркальне 'я' з'явилось");
    }
}