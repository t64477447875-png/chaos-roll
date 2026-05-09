package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class FullDiamondArmorEvent extends BaseEvent {
    @Override public String getId() { return "full_iron_armor"; }
    @Override public String getDisplayName() { return "Залізний обладунок"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 14; }

    @Override
    public void execute(EventContext context) {
        var level = context.world();
        ItemStack helm = EventEnchantUtil.enchant(level, new ItemStack(Items.IRON_HELMET), Enchantments.PROTECTION, 1);
        ItemStack chest = EventEnchantUtil.enchant(level, new ItemStack(Items.IRON_CHESTPLATE), Enchantments.PROTECTION, 1);
        ItemStack legs = EventEnchantUtil.enchant(level, new ItemStack(Items.IRON_LEGGINGS), Enchantments.PROTECTION, 1);
        ItemStack boots = new ItemStack(Items.IRON_BOOTS);
        InventoryUtil.giveOrDrop(context.player(), helm);
        InventoryUtil.giveOrDrop(context.player(), chest);
        InventoryUtil.giveOrDrop(context.player(), legs);
        InventoryUtil.giveOrDrop(context.player(), boots);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Залізний обладунок з Protection I");
    }
}