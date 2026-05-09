package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElytraGiftEvent extends BaseEvent {
    @Override public String getId() { return "elytra_gift"; }
    @Override public String getDisplayName() { return "Елітра в подарунок"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.LEGENDARY; }
    @Override public int getWeight() { return 4; }

    @Override
    public void execute(EventContext context) {
        InventoryUtil.giveOrDrop(context.player(), new ItemStack(Items.ELYTRA));
        InventoryUtil.giveOrDrop(context.player(), new ItemStack(Items.FIREWORK_ROCKET, 8));
        EventNotifyUtil.notifyPlayer(context.player(), this, "Елітра + 8 ракет");
    }
}