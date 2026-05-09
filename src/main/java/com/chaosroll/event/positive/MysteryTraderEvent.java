package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class MysteryTraderEvent extends BaseEvent {
    @Override public String getId() { return "mystery_trader"; }
    @Override public String getDisplayName() { return "Загадковий торговець"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 8; }

    @Override
    public void execute(EventContext context) {
        var level = context.world();
        var player = context.player();
        InventoryUtil.giveOrDrop(player, new ItemStack(Items.EMERALD, 32));
        InventoryUtil.giveOrDrop(player, new ItemStack(Items.NETHERITE_INGOT, 2));
        InventoryUtil.giveOrDrop(player, new ItemStack(Items.NAME_TAG, 3));

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        EventEnchantUtil.enchant(level, book, Enchantments.MENDING, 1);
        InventoryUtil.giveOrDrop(player, book);

        InventoryUtil.giveOrDrop(player, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 3));
        EventNotifyUtil.notifyAll(player, this, "Легендарна торговельна сумка!");
    }
}