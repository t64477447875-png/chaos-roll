package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;

/**
 * Teleports the player straight down to the deepest air block under their current position. If
 * there's a deep cave/void below — perfect death scenario.
 */
public class RandomTpDownEvent extends BaseEvent {
    @Override public String getId() { return "tp_down"; }
    @Override public String getDisplayName() { return "Провал у безодню"; }
    @Override public String getDescription() { return "ТП на найглибшу повітряну точку під тобою. Іноді до самого void."; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var p = context.player();
        var lvl = context.world();
        BlockPos cur = p.blockPosition();
        int targetY = lvl.getMinBuildHeight() + 5;
        // Walk down looking for the deepest air block we can land on.
        for (int y = cur.getY() - 5; y > lvl.getMinBuildHeight() + 1; y--) {
            BlockPos test = new BlockPos(cur.getX(), y, cur.getZ());
            if (lvl.getBlockState(test).isAir() && lvl.getBlockState(test.below()).isAir()) {
                targetY = y;
            }
        }
        p.teleportTo(cur.getX() + 0.5, targetY, cur.getZ() + 0.5);
        EventNotifyUtil.notifyPlayer(p, this, "Тебе провалило в безодню!");
    }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && context.world().dimensionType() != null;
    }
}
