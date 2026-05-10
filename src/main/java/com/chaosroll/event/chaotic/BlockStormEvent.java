package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStormEvent extends BaseEvent {
    private static final BlockState[] BLOCKS = {
            Blocks.SAND.defaultBlockState(),
            Blocks.GRAVEL.defaultBlockState(),
            Blocks.RED_SAND.defaultBlockState(),
            Blocks.ANVIL.defaultBlockState(),
            Blocks.DIRT.defaultBlockState(),
            Blocks.COBBLESTONE.defaultBlockState(),
            Blocks.NETHERRACK.defaultBlockState(),
            Blocks.MOSS_BLOCK.defaultBlockState()
    };

    @Override public String getId() { return "block_storm"; }
    @Override public String getDisplayName() { return "Блок-шторм"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public int getDurationTicks() { return 100; }

    @Override
    public void execute(EventContext context) {
        ServerPlayer player = context.player();
        for (int i = 0; i < 30; i++) {
            int delay = i * 3;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                ServerPlayer p = srv.getPlayerList().getPlayer(player.getUUID());
                if (p == null) return;
                ServerLevel lvl = p.serverLevel();
                double angle = lvl.random.nextDouble() * Math.PI * 2;
                double dist = lvl.random.nextDouble() * 3;
                double x = p.getX() + Math.cos(angle) * dist;
                double z = p.getZ() + Math.sin(angle) * dist;
                double y = p.getY() + 12 + lvl.random.nextDouble() * 6;
                BlockPos pos = BlockPos.containing(x, y, z);
                BlockState block = BLOCKS[lvl.random.nextInt(BLOCKS.length)];
                FallingBlockEntity fbe = FallingBlockEntity.fall(lvl, pos, block);
                fbe.setHurtsEntities(2.0f, 30);
            });
        }
        EventNotifyUtil.notifyPlayer(player, this, "30 блоків падають на голову. Тікай!");
    }
}
