package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.List;

/**
 * Spawns a chest 1 block away from the player with one random item — anywhere from rotten flesh
 * to a netherite ingot. Pure RNG.
 */
public class MysteryGiftEvent extends BaseEvent {

    private static final List<Item> POOL = List.of(
            // Trash
            Items.ROTTEN_FLESH, Items.DIRT, Items.GRAVEL, Items.SAND, Items.STICK, Items.WHEAT_SEEDS,
            // Common
            Items.IRON_INGOT, Items.BREAD, Items.COAL, Items.GOLD_INGOT, Items.LAPIS_LAZULI,
            // Good
            Items.DIAMOND, Items.EMERALD, Items.ENDER_PEARL, Items.GOLDEN_APPLE, Items.ANCIENT_DEBRIS,
            // Rare
            Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP, Items.TOTEM_OF_UNDYING, Items.ENCHANTED_GOLDEN_APPLE,
            Items.ELYTRA, Items.NETHER_STAR
    );

    @Override public String getId() { return "mystery_gift"; }
    @Override public String getDisplayName() { return "Загадковий подарунок"; }
    @Override public String getDescription() { return "Поряд з'являється скриня з одним випадковим предметом — від гнилої плоті до незериту."; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 18; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        var rng = context.random();

        BlockPos pos = player.blockPosition().above().relative(player.getDirection());
        if (!world.getBlockState(pos).canBeReplaced() && !world.getBlockState(pos).isAir()) {
            pos = player.blockPosition().above();
        }
        world.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        if (world.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            int count = 1 + rng.nextInt(3);
            int slot = rng.nextInt(27);
            chest.setItem(slot, new ItemStack(POOL.get(rng.nextInt(POOL.size())), count));
        }
        EventNotifyUtil.notifyPlayer(player, this, "Біля тебе скриня з... сюрпризом!");
    }
}
