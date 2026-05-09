package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;

public class MidnightCurseEvent extends BaseEvent {
    @Override public String getId() { return "midnight_curse"; }
    @Override public String getDisplayName() { return "Опівнічне прокляття"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 4; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        context.world().setDayTime(18000);
        for (int i = 0; i < 3; i++) {
            Phantom p = EntityType.PHANTOM.create(context.world());
            if (p == null) continue;
            p.setPos(player.getX(), player.getY() + 15, player.getZ());
            context.world().addFreshEntity(p);
        }
        EventNotifyUtil.notifyAll(player, this, "Опівніч і 3 фантоми атакують!");
    }
}