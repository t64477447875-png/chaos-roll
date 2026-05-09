package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class DropInventoryEvent extends BaseEvent {
    @Override public String getId() { return "drop_inventory"; }
    @Override public String getDisplayName() { return "Випадання речей"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 25; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        Inventory inv = player.getInventory();
        int dropped = 0;
        int attempts = 0;
        while (dropped < 5 && attempts < 30) {
            attempts++;
            int slot = context.random().nextInt(inv.getContainerSize());
            ItemStack stack = inv.getItem(slot);
            if (stack.isEmpty()) continue;
            inv.removeItemNoUpdate(slot);
            player.drop(stack, true);
            dropped++;
        }
        EventNotifyUtil.notifyPlayer(player, this, dropped + " речей випало навколо");
    }
}