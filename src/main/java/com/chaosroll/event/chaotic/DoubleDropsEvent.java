package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventEnchantUtil;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

public class DoubleDropsEvent extends BaseEvent {
    @Override public String getId() { return "double_drops"; }
    @Override public String getDisplayName() { return "Подвійні дропи"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var level = context.world();
        ItemStack pick = new ItemStack(Items.DIAMOND_PICKAXE);
        EventEnchantUtil.enchant(level, pick, Enchantments.FORTUNE, 3);
        EventEnchantUtil.enchant(level, pick, Enchantments.UNBREAKING, 3);
        EventEnchantUtil.enchant(level, pick, Enchantments.EFFICIENCY, 4);
        InventoryUtil.giveOrDrop(context.player(), pick);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Кірка з Fortune III");
    }
}