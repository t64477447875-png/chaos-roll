package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TotemPackEvent extends BaseEvent {
    @Override public String getId() { return "totem_pack"; }
    @Override public String getDisplayName() { return "Тотеми безсмертя"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 30; }

    @Override
    public void execute(EventContext context) {
        InventoryUtil.giveOrDrop(context.player(), new ItemStack(Items.TOTEM_OF_UNDYING, 3));
        EventNotifyUtil.notifyPlayer(context.player(), this, "+3 тотеми безсмертя");
    }
}