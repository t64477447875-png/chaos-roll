package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnderStashEvent extends BaseEvent {
    @Override public String getId() { return "ender_stash"; }
    @Override public String getDisplayName() { return "Ендер-склад"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 50; }

    @Override
    public void execute(EventContext context) {
        InventoryUtil.giveOrDrop(context.player(), new ItemStack(Items.ENDER_CHEST));
        InventoryUtil.giveOrDrop(context.player(), new ItemStack(Items.ENDER_PEARL, 8));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Ендер-сундук + 8 перлів");
    }
}