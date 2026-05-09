package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafetyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;

public class TreasureRaceEvent extends BaseEvent {

    @Override public String getId() { return "coop_treasure_race"; }
    @Override public String getDisplayName() { return "Перегони за скарбом"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        ServerLevel world = context.world();

        double angle = context.random().nextDouble() * Math.PI * 2.0;
        int distance = 60 + context.random().nextInt(40);
        int targetX = initiator.blockPosition().getX() + (int) (Math.cos(angle) * distance);
        int targetZ = initiator.blockPosition().getZ() + (int) (Math.sin(angle) * distance);

        BlockPos chestPos = SafetyUtil.findSafeY(world, targetX, targetZ);
        if (chestPos == null) {
            BlockPos hint = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    new BlockPos(targetX, 64, targetZ));
            chestPos = hint;
        }

        world.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
        BlockEntity be = world.getBlockEntity(chestPos);
        if (be instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(Items.DIAMOND, 16));
            chest.setItem(1, new ItemStack(Items.NETHERITE_INGOT, 2));
            chest.setItem(2, new ItemStack(Items.EMERALD, 32));
            chest.setItem(13, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 4));
            chest.setItem(26, new ItemStack(Items.TOTEM_OF_UNDYING, 2));
            chest.setChanged();
        }

        for (int i = 0; i < 3; i++) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
            if (bolt == null) break;
            bolt.setVisualOnly(true);
            bolt.moveTo(chestPos.getX() + 0.5, chestPos.getY() + 1, chestPos.getZ() + 0.5);
            world.addFreshEntity(bolt);
        }

        for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
            p.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 1));
        }

        EventNotifyUtil.notifyAll(initiator, this,
                "Скриня з'явилась на " + chestPos.getX() + ", " + chestPos.getY() + ", " + chestPos.getZ()
                        + "! Speed II + Jump II на 30с");
    }
}
