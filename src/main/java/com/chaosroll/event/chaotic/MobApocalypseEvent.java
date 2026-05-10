package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

public class MobApocalypseEvent extends BaseEvent {
    private static final EntityType<?>[] MOBS = {
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER,
            EntityType.HUSK, EntityType.WITCH, EntityType.PILLAGER, EntityType.VINDICATOR,
            EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN,
            EntityType.WOLF, EntityType.BEE, EntityType.RABBIT, EntityType.PARROT,
            EntityType.SLIME, EntityType.PHANTOM, EntityType.DROWNED, EntityType.STRAY
    };

    @Override public String getId() { return "mob_apocalypse"; }
    @Override public String getDisplayName() { return "Мобо-апокаліпсис"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 9; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        ServerLevel world = context.world();
        int spawned = 0;
        for (int i = 0; i < 25; i++) {
            EntityType<?> type = MOBS[world.random.nextInt(MOBS.length)];
            double angle = world.random.nextDouble() * Math.PI * 2;
            double dist = 4 + world.random.nextDouble() * 8;
            double x = player.getX() + Math.cos(angle) * dist;
            double z = player.getZ() + Math.sin(angle) * dist;
            double y = player.getY();
            var entity = type.create(world);
            if (entity != null) {
                entity.moveTo(x, y, z, world.random.nextFloat() * 360f, 0);
                if (entity instanceof net.minecraft.world.entity.Mob mob) {
                    mob.finalizeSpawn(world, world.getCurrentDifficultyAt(mob.blockPosition()),
                            MobSpawnType.EVENT, null);
                }
                world.addFreshEntity(entity);
                spawned++;
            }
        }
        EventNotifyUtil.notifyPlayer(player, this, "25 рандомних мобов спавн! (" + spawned + " вдалося)");
    }
}
