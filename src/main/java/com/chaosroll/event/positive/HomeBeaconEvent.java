package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class HomeBeaconEvent extends BaseEvent {
    @Override public String getId() { return "home_beacon"; }
    @Override public String getDisplayName() { return "Домашній маяк"; }
    @Override public String getDescription() { return "30с — Speed II + Haste II + Resistance II + Regen I"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        BlockPos feet = player.blockPosition();
        BlockPos beaconPos = feet.offset(2, 0, 0);

        BlockState old = world.getBlockState(beaconPos);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos b = beaconPos.below().offset(dx, 0, dz);
                if (world.getBlockState(b).isAir() || world.getBlockState(b).canBeReplaced()) {
                    world.setBlock(b, Blocks.IRON_BLOCK.defaultBlockState(), 3);
                }
            }
        }
        world.setBlock(beaconPos, Blocks.BEACON.defaultBlockState(), 3);

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));

        ScheduledTaskManager.schedule(context.server(), 600, srv -> {
            if (world.getBlockState(beaconPos).is(Blocks.BEACON)) {
                world.setBlock(beaconPos, old, 3);
            }
        });

        EventNotifyUtil.notifyPlayer(player, this, "30с маяк з усіма бафами!");
    }
}
