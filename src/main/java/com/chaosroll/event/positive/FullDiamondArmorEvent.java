package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class FullDiamondArmorEvent extends BaseEvent {
    @Override public String getId() { return "full_diamond_armor"; }
    @Override public String getDisplayName() { return "Повний алмазний обладунок"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 22; }

    @Override
    public void execute(EventContext context) {
        var level = context.world();
        ItemStack helm = EventEnchantUtil.enchant(level, new ItemStack(Items.DIAMOND_HELMET), Enchantments.PROTECTION, 4);
        ItemStack chest = EventEnchantUtil.enchant(level, new ItemStack(Items.DIAMOND_CHESTPLATE), Enchantments.PROTECTION, 4);
        ItemStack legs = EventEnchantUtil.enchant(level, new ItemStack(Items.DIAMOND_LEGGINGS), Enchantments.PROTECTION, 4);
        ItemStack boots = EventEnchantUtil.enchant(level, new ItemStack(Items.DIAMOND_BOOTS), Enchantments.PROTECTION, 4);
        EventEnchantUtil.enchant(level, boots, Enchantments.FEATHER_FALLING, 4);
        InventoryUtil.giveOrDrop(context.player(), helm);
        InventoryUtil.giveOrDrop(context.player(), chest);
        InventoryUtil.giveOrDrop(context.player(), legs);
        InventoryUtil.giveOrDrop(context.player(), boots);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Повний обладунок з Protection IV");
    }
}