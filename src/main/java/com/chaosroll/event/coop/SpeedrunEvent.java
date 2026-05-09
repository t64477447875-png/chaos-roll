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
import net.minecraft.world.level.block.Blocks;

public class SpeedrunEvent extends BaseEvent {

    @Override public String getId() { return "coop_speedrun"; }
    @Override public String getDisplayName() { return "Speedrun-маркер"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 22; }
    @Override public boolean isGlobal() { return true; }
    @Override public int getDurationTicks() { return 600; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        ServerLevel world = context.world();

        double angle = context.random().nextDouble() * Math.PI * 2.0;
        int distance = 50 + context.random().nextInt(20);
        int targetX = initiator.blockPosition().getX() + (int) (Math.cos(angle) * distance);
        int targetZ = initiator.blockPosition().getZ() + (int) (Math.sin(angle) * distance);

        BlockPos pos = SafetyUtil.findSafeY(world, targetX, targetZ);
        if (pos == null) {
            pos = new BlockPos(targetX, world.getMaxBuildHeight() - 30, targetZ);
        }

        for (int dy = 0; dy < 6; dy++) {
            world.setBlock(pos.offset(0, dy, 0), Blocks.BEACON.defaultBlockState(), 3);
        }
        world.setBlock(pos.offset(0, 5, 0), Blocks.EMERALD_BLOCK.defaultBlockState(), 3);

        int endTick = context.server().getTickCount() + 600;
        CoopState.SPEEDRUN = new CoopState.SpeedrunSession(pos.getX(), pos.getY(), pos.getZ(), endTick);

        for (ServerPlayer p : context.server().getPlayerList().getPlayers()) {
            p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 2));
            p.addEffect(new MobEffectInstance(MobEffects.JUMP, 600, 1));
        }

        EventNotifyUtil.notifyAll(initiator, this,
                "Маркер на " + pos.getX() + "," + pos.getY() + "," + pos.getZ()
                        + " — добіжи за 30с! Speed III всім");
    }
}
