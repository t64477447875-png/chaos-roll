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
            new ItemStack(Items.DIAMOND, 2),
            new ItemStack(Items.GOLD_INGOT, 6),
            new ItemStack(Items.IRON_INGOT, 8),
            new ItemStack(Items.GOLDEN_APPLE, 1),
            new ItemStack(Items.ENDER_PEARL, 2),
            new ItemStack(Items.EXPERIENCE_BOTTLE, 4),
            new ItemStack(Items.OBSIDIAN, 4),
            new ItemStack(Items.GLOWSTONE, 4),
            new ItemStack(Items.BREAD, 8),
            new ItemStack(Items.ARROW, 16)
    );

    @Override public String getId() { return "shulker_surprise"; }
    @Override public String getDisplayName() { return "Шалкер-сюрприз"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 5; }

    @Override
    public void execute(EventContext context) {
        ItemStack box = new ItemStack(Items.PURPLE_SHULKER_BOX);
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            contents.add(POOL.get(context.random().nextInt(POOL.size())).copy());
        }
        box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
        InventoryUtil.giveOrDrop(context.player(), box);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Шалкер з 4 предметами");
    }
}