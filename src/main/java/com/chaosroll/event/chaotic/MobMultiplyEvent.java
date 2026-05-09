package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class MobMultiplyEvent extends BaseEvent {
    @Override public String getId() { return "mob_multiply"; }
    @Override public String getDisplayName() { return "Розмноження мобів"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 18; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        ServerLevel world = context.world();
        AABB box = player.getBoundingBox().inflate(20);
        List<LivingEntity> mobs = world.getEntitiesOfClass(LivingEntity.class, box,
                e -> e instanceof Mob && !(e instanceof Player));
        if (mobs.isEmpty()) {
            EventNotifyUtil.notifyPlayer(player, this, "Немає мобів поруч — пропуск");
            return;
        }
        LivingEntity target = mobs.get(context.random().nextInt(mobs.size()));
        EntityType<?> type = target.getType();
        for (int i = 0; i < 5; i++) {
            Entity copy = type.create(world);
            if (copy == null) continue;
            copy.setPos(target.getX() + (context.random().nextDouble() - 0.5) * 3,
                    target.getY(),
                    target.getZ() + (context.random().nextDouble() - 0.5) * 3);
            world.addFreshEntity(copy);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Знайдено " + target.getName().getString() + " — створено 5 копій");
    }
}
