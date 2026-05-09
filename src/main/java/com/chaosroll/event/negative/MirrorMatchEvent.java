package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MirrorMatchEvent extends BaseEvent {
    @Override public String getId() { return "mirror_match"; }
    @Override public String getDisplayName() { return "Mirror Match"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        Husk copy = EntityType.HUSK.create(context.world());
        if (copy == null) return;
        copy.setPos(player.getX() + 2, player.getY(), player.getZ() + 2);
        copy.setCustomName(Component.literal("Тінь " + player.getName().getString()));
        copy.setCustomNameVisible(true);

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack worn = player.getItemBySlot(slot);
            if (!worn.isEmpty()) {
                ItemStack copyStack = worn.copy();
                copyStack.set(DataComponents.CUSTOM_NAME, Component.literal("Тінь"));
                copy.setItemSlot(slot, copyStack);
                copy.setDropChance(slot, 0.0f);
            }
        }
        ItemStack hand = player.getMainHandItem().copy();
        if (hand.isEmpty()) hand = new ItemStack(Items.IRON_SWORD);
        copy.setItemSlot(EquipmentSlot.MAINHAND, hand);
        copy.setDropChance(EquipmentSlot.MAINHAND, 0.0f);

        AttributeInstance maxHp = copy.getAttribute(Attributes.MAX_HEALTH);
        if (maxHp != null) {
            maxHp.setBaseValue(Math.max(20.0, player.getMaxHealth()));
            copy.setHealth((float) maxHp.getValue());
        }
        AttributeInstance follow = copy.getAttribute(Attributes.FOLLOW_RANGE);
        if (follow != null) follow.setBaseValue(64.0);
        copy.setTarget(player);
        copy.setPersistenceRequired();
        context.world().addFreshEntity(copy);

        EventNotifyUtil.notifyPlayer(player, this, "Твоя тінь з'явилась з твоєю бронею і мечем!");
    }
}
