package com.chaosroll.event;

import com.chaosroll.event.coop.CoopState;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

    private EventHooks() {}

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(EventHooks::onBlockBreak);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(EventHooks::onAllowDamage);
    }

    private static void onBlockBreak(Level world, Player player, BlockPos pos,
                                     net.minecraft.world.level.block.state.BlockState state,
                                     BlockEntity blockEntity) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (world.isClientSide) return;
        Integer endTick = CoopState.BLOCK_ROULETTE.get(sp.getUUID());
        if (endTick == null) return;
        if (sp.getServer() == null || sp.getServer().getTickCount() >= endTick) {
            CoopState.BLOCK_ROULETTE.remove(sp.getUUID());
            return;
        }
        net.minecraft.world.item.Item drop = ROULETTE_DROPS[RNG.nextInt(ROULETTE_DROPS.length)];
        net.minecraft.world.entity.item.ItemEntity entity =
                new net.minecraft.world.entity.item.ItemEntity(world,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        new ItemStack(drop));
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setPickUpDelay(10);
        world.addFreshEntity(entity);
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
