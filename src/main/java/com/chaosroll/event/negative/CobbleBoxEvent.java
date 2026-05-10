package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

public class CobbleBoxEvent extends BaseEvent {
    @Override public String getId() { return "cobble_box"; }
    @Override public String getDisplayName() { return "Кам'яна тюрма"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 14; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        BlockPos feet = player.blockPosition();
        int[][] offsets = {
                {-1, 0, 0}, {1, 0, 0}, {0, 0, -1}, {0, 0, 1},
                {-1, 1, 0}, {1, 1, 0}, {0, 1, -1}, {0, 1, 1},
                {0, 2, 0}, {0, -1, 0}
        };
        for (int[] o : offsets) {
            BlockPos pos = feet.offset(o[0], o[1], o[2]);
            if (world.getBlockState(pos).canBeReplaced()) {
                world.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
            } else if (world.getBlockState(pos).isAir()) {
                world.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
            }
        }
        EventNotifyUtil.notifyPlayer(player, this, "Тюрма з cobblestone — мини або задихайся!");
    }
}
