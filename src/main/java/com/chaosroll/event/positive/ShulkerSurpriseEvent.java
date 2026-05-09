package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

public class ShulkerSurpriseEvent extends BaseEvent {
    private static final List<ItemStack> POOL = List.of(
            new ItemStack(Items.DIAMOND, 16),
            new ItemStack(Items.GOLD_INGOT, 32),
            new ItemStack(Items.IRON_INGOT, 32),
            new ItemStack(Items.NETHERITE_SCRAP, 4),
            new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 2),
            new ItemStack(Items.TOTEM_OF_UNDYING, 1),
            new ItemStack(Items.ENDER_PEARL, 8),
            new ItemStack(Items.EXPERIENCE_BOTTLE, 16),
            new ItemStack(Items.OBSIDIAN, 16),
            new ItemStack(Items.GLOWSTONE, 16)
    );

    @Override public String getId() { return "shulker_surprise"; }
    @Override public String getDisplayName() { return "Шалкер-сюрприз"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        ItemStack box = new ItemStack(Items.PURPLE_SHULKER_BOX);
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            contents.add(POOL.get(context.random().nextInt(POOL.size())).copy());
        }
        box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
        InventoryUtil.giveOrDrop(context.player(), box);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Шалкер з 8 цінних речей");
    }
}