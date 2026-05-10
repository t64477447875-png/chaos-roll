package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.event.coop.CoopState;
import com.chaosroll.network.MorphPacket;
import com.chaosroll.util.EventNotifyUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class MobMorphEvent extends BaseEvent {

    private static final List<EntityType<? extends Mob>> POOL = List.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.SPIDER, EntityType.PIG, EntityType.COW,
            EntityType.SHEEP, EntityType.CHICKEN, EntityType.WOLF,
            EntityType.VILLAGER, EntityType.ENDERMAN
    );

    @Override public String getId() { return "mob_morph"; }
    @Override public String getDisplayName() { return "Морф"; }
    @Override public String getDescription() { return "60с — ти стаєш випадковим мобом для всіх інших"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 1200; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        var rng = context.random();
        EntityType<? extends Mob> type = POOL.get(rng.nextInt(POOL.size()));

        Mob mob = type.create(world);
        if (mob == null) {
            EventNotifyUtil.notifyPlayer(player, this, "Морф не вдався (failed to spawn).");
            return;
        }
        mob.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.setCustomName(player.getName());
        mob.setCustomNameVisible(true);
        mob.setSilent(true);
        if (mob.canBeCollidedWith()) {
            mob.noPhysics = true;
        }
        world.addFreshEntity(mob);

        int duration = getDurationTicks();
        // Long-duration invisibility so other players see only the mob.
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false));

        int endTick = context.server().getTickCount() + duration;
        CoopState.MORPH.put(player.getUUID(), new CoopState.MorphSession(endTick, mob.getId(), mob.getUUID()));

        // Notify the client to switch into third-person and adopt the mob's eye height. This makes
        // morph feel real: the player sees the mob as their own avatar instead of just being invisible.
        String mobKey = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        float mobEyeHeight = mob.getEyeHeight();
        ServerPlayNetworking.send(player, new MorphPacket(duration, mobKey, mobEyeHeight));

        EventNotifyUtil.notifyPlayer(player, this,
                "60с — ти РЕАЛЬНО став " + type.getDescription().getString() + "! (камера від 3-ї особи)");
    }
}
