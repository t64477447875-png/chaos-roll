package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;

public class AnvilCurseEvent extends BaseEvent {
    @Override public String getId() { return "anvil_curse"; }
    @Override public String getDisplayName() { return "Прокляття ковадла"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        player.setHealth(player.getMaxHealth());
        BlockPos pos = new BlockPos((int) player.getX(), (int) player.getY() + 5, (int) player.getZ());
        FallingBlockEntity.fall(context.world(), pos, Blocks.ANVIL.defaultBlockState());
        EventNotifyUtil.notifyPlayer(player, this, "Ковадло падає згори!");
    }
}