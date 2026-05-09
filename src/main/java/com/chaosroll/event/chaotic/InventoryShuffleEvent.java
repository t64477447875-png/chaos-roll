package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.item.ItemStack;

public class InventoryShuffleEvent extends BaseEvent {
    @Override public String getId() { return "inventory_shuffle"; }
    @Override public String getDisplayName() { return "Перетасовка інвентарю"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 22; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var inv = player.getInventory();
        int size = inv.items.size();
        for (int i = 0; i < size; i++) {
            int j = context.random().nextInt(size);
            ItemStack a = inv.items.get(i);
            ItemStack b = inv.items.get(j);
            inv.items.set(i, b);
            inv.items.set(j, a);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Інвентар перемішано!");
    }
}
