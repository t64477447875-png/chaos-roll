package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantedToolsEvent extends BaseEvent {
    @Override public String getId() { return "enchanted_tools"; }
    @Override public String getDisplayName() { return "Зачарований набір"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 22; }

    @Override
    public void execute(EventContext context) {
        var level = context.world();
        ItemStack pick = new ItemStack(Items.DIAMOND_PICKAXE);
        EventEnchantUtil.enchant(level, pick, Enchantments.EFFICIENCY, 5);
        EventEnchantUtil.enchant(level, pick, Enchantments.UNBREAKING, 3);
        EventEnchantUtil.enchant(level, pick, Enchantments.FORTUNE, 3);

        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        EventEnchantUtil.enchant(level, sword, Enchantments.SHARPNESS, 5);
        EventEnchantUtil.enchant(level, sword, Enchantments.UNBREAKING, 3);
        EventEnchantUtil.enchant(level, sword, Enchantments.LOOTING, 3);

        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        EventEnchantUtil.enchant(level, axe, Enchantments.EFFICIENCY, 5);
        EventEnchantUtil.enchant(level, axe, Enchantments.UNBREAKING, 3);

        InventoryUtil.giveOrDrop(context.player(), pick);
        InventoryUtil.giveOrDrop(context.player(), sword);
        InventoryUtil.giveOrDrop(context.player(), axe);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Кірка + меч + сокира з чарами");
    }
}