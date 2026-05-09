package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

public class MysteryBoxEvent extends BaseEvent {
    private static final List<ItemStack> POOL = List.of(
            new ItemStack(Items.DIAMOND, 4), new ItemStack(Items.ROTTEN_FLESH, 16),
            new ItemStack(Items.GOLD_INGOT, 8), new ItemStack(Items.DIRT, 32),
            new ItemStack(Items.EMERALD, 4), new ItemStack(Items.POISONOUS_POTATO, 4),
            new ItemStack(Items.NETHERITE_SCRAP, 1), new ItemStack(Items.STICK, 16),
            new ItemStack(Items.TOTEM_OF_UNDYING, 1), new ItemStack(Items.COBBLESTONE, 64)
    );

    @Override public String getId() { return "mystery_box"; }
    @Override public String getDisplayName() { return "Загадкова коробка"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        ItemStack box = new ItemStack(Items.LIGHT_BLUE_SHULKER_BOX);
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            contents.add(POOL.get(context.random().nextInt(POOL.size())).copy());
        }
        box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
        InventoryUtil.giveOrDrop(context.player(), box);
        EventNotifyUtil.notifyPlayer(context.player(), this, "Коробка з 6 невідомими речами");
    }
}