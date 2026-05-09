package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperPartyEvent extends BaseEvent {
    @Override public String getId() { return "creeper_party"; }
    @Override public String getDisplayName() { return "Кріпер-вечірка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 10; i++) {
            Creeper c = EntityType.CREEPER.create(context.world());
            if (c == null) continue;
            double angle = (Math.PI * 2 * i) / 10;
            double r = 4 + context.random().nextDouble() * 2;
            c.setPos(player.getX() + Math.cos(angle) * r, player.getY(),
                    player.getZ() + Math.sin(angle) * r);
            c.setPersistenceRequired();
            if (i < 3) {
                CompoundTag tag = new CompoundTag();
                tag.putBoolean("powered", true);
                c.readAdditionalSaveData(tag);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(context.world());
                if (bolt != null) {
                    bolt.setVisualOnly(true);
                    bolt.moveTo(c.getX(), c.getY(), c.getZ());
                    context.world().addFreshEntity(bolt);
                }
            }
            context.world().addFreshEntity(c);
        }
        EventNotifyUtil.notifyPlayer(player, this, "10 кріперів (3 заряджених) на 4 блоки!");
    }
}
