package com.chaosroll.event;

import com.chaosroll.event.coop.CoopState;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public final class EventHooks {

    private static final Random RNG = new Random();

    private static final net.minecraft.world.item.Item[] ROULETTE_DROPS = new net.minecraft.world.item.Item[]{
            Items.DIAMOND, Items.IRON_INGOT, Items.GOLD_INGOT, Items.EMERALD,
            Items.DIRT, Items.COBBLESTONE, Items.OAK_LOG, Items.STONE,
            Items.CACTUS, Items.SAND, Items.NETHERRACK, Items.BONE,
            Items.ROTTEN_FLESH, Items.GUNPOWDER, Items.SLIME_BALL, Items.STRING,
            Items.TNT, Items.OBSIDIAN, Items.LAPIS_LAZULI, Items.REDSTONE,
            Items.WHEAT_SEEDS, Items.GLASS, Items.WHITE_WOOL, Items.PAPER
    };

    private static final net.minecraft.world.item.Item[] RANDOM_LOOT_POOL = new net.minecraft.world.item.Item[]{
            Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT,
            Items.NETHERITE_SCRAP, Items.ENDER_PEARL, Items.BLAZE_ROD,
            Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING,
            Items.NAUTILUS_SHELL, Items.HEART_OF_THE_SEA, Items.EXPERIENCE_BOTTLE,
            Items.GHAST_TEAR, Items.NETHER_STAR, Items.BLAZE_POWDER,
            Items.REDSTONE, Items.LAPIS_LAZULI, Items.GLOWSTONE_DUST,
            Items.STICK, Items.STRING, Items.FEATHER, Items.WHEAT_SEEDS,
            Items.ROTTEN_FLESH, Items.BONE, Items.GUNPOWDER, Items.SPIDER_EYE,
            Items.DIRT, Items.SAND, Items.GRAVEL, Items.PUMPKIN_SEEDS,
            Items.MELON_SLICE, Items.APPLE, Items.CARROT, Items.POTATO,
            Items.COOKED_BEEF, Items.COOKED_PORKCHOP, Items.GOLDEN_CARROT,
            Items.BREAD, Items.SUGAR_CANE, Items.BAMBOO, Items.FIREWORK_ROCKET,
            Items.SADDLE, Items.NAME_TAG, Items.MUSIC_DISC_CAT, Items.OAK_LOG,
            Items.AMETHYST_SHARD, Items.ECHO_SHARD
    };

    private EventHooks() {}

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(EventHooks::onBlockBreak);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(EventHooks::onAllowDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(EventHooks::onMobDeath);
    }

    private static void onBlockBreak(Level world, Player player, BlockPos pos,
                                     net.minecraft.world.level.block.state.BlockState state,
                                     BlockEntity blockEntity) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (world.isClientSide) return;
        long now = sp.getServer() == null ? 0 : sp.getServer().getTickCount();

        Integer rouletteEnd = CoopState.BLOCK_ROULETTE.get(sp.getUUID());
        if (rouletteEnd != null && now < rouletteEnd) {
            net.minecraft.world.item.Item drop = ROULETTE_DROPS[RNG.nextInt(ROULETTE_DROPS.length)];
            net.minecraft.world.entity.item.ItemEntity entity =
                    new net.minecraft.world.entity.item.ItemEntity(world,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            new ItemStack(drop));
            entity.setDeltaMovement(Vec3.ZERO);
            entity.setPickUpDelay(10);
            world.addFreshEntity(entity);
        } else if (rouletteEnd != null) {
            CoopState.BLOCK_ROULETTE.remove(sp.getUUID());
        }

        Integer doubleEnd = CoopState.DOUBLE_DROPS.get(sp.getUUID());
        if (doubleEnd != null && now < doubleEnd && world instanceof ServerLevel sl) {
            Block.dropResources(state, sl, pos, blockEntity, sp, sp.getMainHandItem());
        } else if (doubleEnd != null) {
            CoopState.DOUBLE_DROPS.remove(sp.getUUID());
        }

        Integer rndEnd = CoopState.RANDOM_LOOT.get(sp.getUUID());
        if (rndEnd != null && now < rndEnd) {
            spawnRandomLoot(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        } else if (rndEnd != null) {
            CoopState.RANDOM_LOOT.remove(sp.getUUID());
        }
    }

    private static void onMobDeath(LivingEntity victim, net.minecraft.world.damagesource.DamageSource source) {
        if (victim instanceof Player) return;
        if (!(source.getEntity() instanceof ServerPlayer killer)) return;
        if (killer.getServer() == null) return;
        Integer rndEnd = CoopState.RANDOM_LOOT.get(killer.getUUID());
        if (rndEnd == null) return;
        if (killer.getServer().getTickCount() >= rndEnd) {
            CoopState.RANDOM_LOOT.remove(killer.getUUID());
            return;
        }
        spawnRandomLoot(victim.level(), victim.getX(), victim.getY() + 0.5, victim.getZ());
    }

    private static void spawnRandomLoot(Level world, double x, double y, double z) {
        net.minecraft.world.item.Item drop = RANDOM_LOOT_POOL[RNG.nextInt(RANDOM_LOOT_POOL.length)];
        int count = 1;
        if (drop == Items.DIRT || drop == Items.COBBLESTONE || drop == Items.STONE
                || drop == Items.SAND || drop == Items.GRAVEL || drop == Items.OAK_LOG
                || drop == Items.STRING || drop == Items.STICK || drop == Items.WHEAT_SEEDS
                || drop == Items.REDSTONE || drop == Items.LAPIS_LAZULI) {
            count = 1 + RNG.nextInt(3);
        }
        net.minecraft.world.entity.item.ItemEntity e =
                new net.minecraft.world.entity.item.ItemEntity(world, x, y, z,
                        new ItemStack(drop, count));
        e.setDeltaMovement(Vec3.ZERO);
        e.setPickUpDelay(10);
        world.addFreshEntity(e);
    }

    private static boolean onAllowDamage(LivingEntity victim, net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (!(source.getEntity() instanceof ServerPlayer attacker)) return true;
        if (victim instanceof Player) return true;
        Integer endTick = CoopState.CURSED_DAMAGE.get(attacker.getUUID());
        if (endTick == null) return true;
        if (attacker.getServer() == null || attacker.getServer().getTickCount() >= endTick) {
            CoopState.CURSED_DAMAGE.remove(attacker.getUUID());
            return true;
        }
        victim.heal(amount);
        attacker.hurt(attacker.damageSources().magic(), amount);
        return false;
    }
}
