package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class TreasureBurstEvent extends BaseEvent {
    private static final net.minecraft.world.item.Item[] LOOT = {
            Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT, Items.EMERALD,
            Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.ENDER_PEARL,
            Items.NETHERITE_INGOT, Items.TOTEM_OF_UNDYING, Items.ELYTRA,
            Items.BLAZE_ROD, Items.NETHER_STAR, Items.EXPERIENCE_BOTTLE
    };

    @Override public String getId() { return "treasure_burst"; }
    @Override public String getDisplayName() { return "Скриня з скарбами"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        ServerLevel world = context.world();
        BlockPos pos = player.blockPosition().relative(player.getDirection(), 2);
        if (!world.getBlockState(pos).canBeReplaced() && !world.getBlockState(pos).isAir()) {
            pos = player.blockPosition();
        }
        world.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            int items = 6 + player.getRandom().nextInt(3);
            for (int i = 0; i < items; i++) {
                var item = LOOT[player.getRandom().nextInt(LOOT.length)];
                int count = 1;
                if (item == Items.DIAMOND || item == Items.EMERALD || item == Items.GOLD_INGOT
                        || item == Items.IRON_INGOT || item == Items.EXPERIENCE_BOTTLE) {
                    count = 2 + player.getRandom().nextInt(4);
                }
                ItemStack stack = new ItemStack(item, count);
                chest.setItem(player.getRandom().nextInt(27), stack);
            }
        }
        EventNotifyUtil.notifyPlayer(player, this, "Скриня з скарбами поряд!");
    }
}
