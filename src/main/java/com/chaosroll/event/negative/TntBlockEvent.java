package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.block.Blocks;

public class TntBlockEvent extends BaseEvent {
    @Override public String getId() { return "tnt_block"; }
    @Override public String getDisplayName() { return "TNT під ногами"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 18; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        BlockPos under = player.blockPosition().below();
        if (!world.getBlockState(under).isAir() && !world.getBlockState(under).is(Blocks.BEDROCK)) {
            world.setBlock(under, Blocks.TNT.defaultBlockState(), 3);
        }
        PrimedTnt tnt = new PrimedTnt(world, under.getX() + 0.5, under.getY() + 1.0, under.getZ() + 0.5, player);
        tnt.setFuse(40);
        world.addFreshEntity(tnt);
        EventNotifyUtil.notifyPlayer(player, this, "TNT під ногами! 2 секунди — тікай!");
    }
}
