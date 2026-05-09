package com.chaosroll.event.positive;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class RandomLootEvent extends BaseEvent {
    private static final List<ItemStack> POOL = List.of(
            new ItemStack(Items.DIAMOND, 8),
            new ItemStack(Items.EMERALD, 16),
            new ItemStack(Items.GOLD_INGOT, 24),
            new ItemStack(Items.IRON_INGOT, 32),
            new ItemStack(Items.NETHERITE_INGOT, 1),
            new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2),
            new ItemStack(Items.OBSIDIAN, 16),
            new ItemStack(Items.ANCIENT_DEBRIS, 2),
            new ItemStack(Items.EXPERIENCE_BOTTLE, 16)
    );

    @Override public String getId() { return "random_loot"; }
    @Override public String getDisplayName() { return "Випадковий лут"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 50; }

    @Override
    public void execute(EventContext context) {
        for (int i = 0; i < 3; i++) {
            ItemStack stack = POOL.get(context.random().nextInt(POOL.size())).copy();
            InventoryUtil.giveOrDrop(context.player(), stack);
        }
        EventNotifyUtil.notifyPlayer(context.player(), this, "+3 цінні предмети");
    }
}