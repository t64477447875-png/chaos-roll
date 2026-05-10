package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;

/**
 * Drops 6 anvils from the sky onto the player at staggered intervals. Helmet helps; not having
 * one is fatal.
 */
public class CursedAnvilEvent extends BaseEvent {
    @Override public String getId() { return "cursed_anvil"; }
    @Override public String getDisplayName() { return "Прокляті ковадла"; }
    @Override public String getDescription() { return "6 ковадл падають з неба на голову. Шолом обов'язковий."; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var rng = context.random();
        java.util.UUID id = player.getUUID();
        for (int i = 0; i < 6; i++) {
            int delay = i * 12;
            ScheduledTaskManager.schedule(context.server(), delay, srv -> {
                var target = srv.getPlayerList().getPlayer(id);
                if (target == null) return;
                double dx = (rng.nextDouble() - 0.5) * 2;
                double dz = (rng.nextDouble() - 0.5) * 2;
                BlockPos top = new BlockPos(
                        (int) (target.getX() + dx),
                        (int) (target.getY() + 18),
                        (int) (target.getZ() + dz));
                FallingBlockEntity fbe = FallingBlockEntity.fall(
                        target.serverLevel(), top, Blocks.ANVIL.defaultBlockState());
                fbe.setHurtsEntities(2.0f, 40);
            });
        }
        EventNotifyUtil.notifyPlayer(player, this, "Ковадла з неба — 6 шт.!");
    }
}
