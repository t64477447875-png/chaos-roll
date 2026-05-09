package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ItemRainEvent extends BaseEvent {
    private static final List<ItemStack> POOL = List.of(
            new ItemStack(Items.GOLD_INGOT, 4), new ItemStack(Items.IRON_INGOT, 8),
            new ItemStack(Items.DIAMOND, 1), new ItemStack(Items.EMERALD, 2),
            new ItemStack(Items.APPLE, 8), new ItemStack(Items.BREAD, 8),
            new ItemStack(Items.COBBLESTONE, 32), new ItemStack(Items.OAK_LOG, 16)
    );

    @Override public String getId() { return "item_rain"; }
    @Override public String getDisplayName() { return "Дощ з предметів"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 15; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 12; i++) {
            ItemStack stack = POOL.get(context.random().nextInt(POOL.size())).copy();
            double dx = (context.random().nextDouble() - 0.5) * 6;
            double dz = (context.random().nextDouble() - 0.5) * 6;
            ItemEntity ie = new ItemEntity(context.world(),
                    player.getX() + dx, player.getY() + 12, player.getZ() + dz, stack);
            context.world().addFreshEntity(ie);
        }
        EventNotifyUtil.notifyPlayer(player, this, "З неба падає 12 предметів");
    }
}