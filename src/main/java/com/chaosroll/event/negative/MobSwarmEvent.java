package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MobSwarmEvent extends BaseEvent {
    @Override public String getId() { return "mob_swarm"; }
    @Override public String getDisplayName() { return "Орда мерців"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 35; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 12; i++) {
            Zombie z = EntityType.ZOMBIE.create(context.world());
            if (z == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 8;
            double dz = (context.random().nextDouble() - 0.5) * 8;
            z.setPos(player.getX() + dx, player.getY(), player.getZ() + dz);
            z.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            z.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            z.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            z.setPersistenceRequired();
            context.world().addFreshEntity(z);
        }
        for (int i = 0; i < 3; i++) {
            Husk h = EntityType.HUSK.create(context.world());
            if (h == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 6;
            double dz = (context.random().nextDouble() - 0.5) * 6;
            h.setPos(player.getX() + dx, player.getY(), player.getZ() + dz);
            h.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_AXE));
            h.setPersistenceRequired();
            context.world().addFreshEntity(h);
        }
        for (int i = 0; i < 4; i++) {
            Skeleton s = EntityType.SKELETON.create(context.world());
            if (s == null) continue;
            double angle = context.random().nextDouble() * Math.PI * 2;
            double r = 6;
            s.setPos(player.getX() + Math.cos(angle) * r, player.getY(),
                    player.getZ() + Math.sin(angle) * r);
            s.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
            s.setPersistenceRequired();
            context.world().addFreshEntity(s);
        }
        EventNotifyUtil.notifyPlayer(player, this, "12 зомбі + 3 гасків + 4 скелети!");
    }
}
