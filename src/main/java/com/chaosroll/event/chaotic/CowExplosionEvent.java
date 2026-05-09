package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;

public class CowExplosionEvent extends BaseEvent {
    @Override public String getId() { return "cow_explosion"; }
    @Override public String getDisplayName() { return "Корівник"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 30; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 15; i++) {
            Cow c = EntityType.COW.create(context.world());
            if (c == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 4;
            double dz = (context.random().nextDouble() - 0.5) * 4;
            c.setPos(player.getX() + dx, player.getY() + 5, player.getZ() + dz);
            context.world().addFreshEntity(c);
        }
        EventNotifyUtil.notifyPlayer(player, this, "15 корів падають з неба!");
    }
}
