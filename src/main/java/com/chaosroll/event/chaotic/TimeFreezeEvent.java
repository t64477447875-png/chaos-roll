package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

/**
 * Freezes all hostile and passive mobs in a 30-block radius for 60s by setting noAi = true.
 * Restores AI after.
 */
public class TimeFreezeEvent extends BaseEvent {
    @Override public String getId() { return "time_freeze"; }
    @Override public String getDisplayName() { return "Заморозка часу"; }
    @Override public String getDescription() { return "60с — мобам у радіусі 30 блоків зупиняється AI (не рухаються, не атакують)."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        AABB box = new AABB(player.blockPosition()).inflate(30);
        var mobs = world.getEntitiesOfClass(Mob.class, box);
        java.util.List<java.util.UUID> frozen = new java.util.ArrayList<>(mobs.size());
        for (Mob m : mobs) {
            if (m.isNoAi()) continue;
            m.setNoAi(true);
            frozen.add(m.getUUID());
        }
        ScheduledTaskManager.schedule(context.server(), 1200, srv -> {
            var lvl = player.serverLevel();
            for (java.util.UUID id : frozen) {
                if (lvl.getEntity(id) instanceof Mob m) {
                    m.setNoAi(false);
                }
            }
        });
        EventNotifyUtil.notifyPlayer(player, this, "Заморожено " + frozen.size() + " мобів на 60 сек!");
    }
}
