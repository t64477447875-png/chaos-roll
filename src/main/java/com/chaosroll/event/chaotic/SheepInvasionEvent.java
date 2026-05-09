package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepInvasionEvent extends BaseEvent {
    @Override public String getId() { return "sheep_invasion"; }
    @Override public String getDisplayName() { return "Овече вторгнення"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        DyeColor[] colors = DyeColor.values();
        for (int i = 0; i < 10; i++) {
            Sheep s = EntityType.SHEEP.create(context.world());
            if (s == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 6;
            double dz = (context.random().nextDouble() - 0.5) * 6;
            s.setPos(player.getX() + dx, player.getY(), player.getZ() + dz);
            s.setColor(colors[context.random().nextInt(colors.length)]);
            context.world().addFreshEntity(s);
        }
        EventNotifyUtil.notifyPlayer(player, this, "10 кольорових овець!");
    }
}