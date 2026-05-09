package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;

import java.util.UUID;

public class AnvilCurseEvent extends BaseEvent {
    @Override public String getId() { return "anvil_curse"; }
    @Override public String getDisplayName() { return "Прокляття ковадла"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        UUID id = context.player().getUUID();
        for (int i = 0; i < 5; i++) {
            int delay = i * 15;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                ServerPlayer target = srv.getPlayerList().getPlayer(id);
                if (target == null) return;
                double dx = (target.getRandom().nextDouble() - 0.5) * 3;
                double dz = (target.getRandom().nextDouble() - 0.5) * 3;
                BlockPos pos = new BlockPos(
                        (int) (target.getX() + dx),
                        (int) target.getY() + 8,
                        (int) (target.getZ() + dz));
                FallingBlockEntity.fall(target.serverLevel(), pos, Blocks.ANVIL.defaultBlockState());
            });
        }
        EventNotifyUtil.notifyPlayer(context.player(), this, "5 ковадел падають згори!");
    }
}
