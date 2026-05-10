package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class InventoryRecycleEvent extends BaseEvent {
    private static final net.minecraft.world.item.Item[] POOL = new net.minecraft.world.item.Item[]{
            Items.CHICKEN, Items.ROTTEN_FLESH, Items.DIRT, Items.GUNPOWDER, Items.STICK,
            Items.BONE, Items.STRING, Items.SAND, Items.GRAVEL, Items.COBBLESTONE,
            Items.REDSTONE, Items.LAPIS_LAZULI, Items.OAK_LOG, Items.WHEAT_SEEDS, Items.PAPER,
            Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD, Items.GLASS,
            Items.WHITE_WOOL, Items.SLIME_BALL, Items.BLAZE_ROD, Items.MELON_SLICE, Items.APPLE,
            Items.SUGAR_CANE, Items.NETHERRACK, Items.CACTUS, Items.ARROW, Items.FEATHER
    };

    @Override public String getId() { return "inventory_recycle"; }
    @Override public String getDisplayName() { return "Рециклінг інвентаря"; }
    @Override public String getDescription() { return "Половина інвентаря миттєво стає випадковими предметами"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 9; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var rng = context.random();
        Inventory inv = player.getInventory();
        int converted = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (rng.nextFloat() < 0.5f) {
                int count = stack.getCount();
                inv.setItem(i, new ItemStack(POOL[rng.nextInt(POOL.length)], count));
                converted++;
            }
        }
        player.containerMenu.broadcastChanges();
        EventNotifyUtil.notifyPlayer(player, this, converted + " слотів інвентаря миттєво конвертовано!");
    }
}
