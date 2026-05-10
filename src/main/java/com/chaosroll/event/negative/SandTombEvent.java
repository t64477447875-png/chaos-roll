package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;

public class SandTombEvent extends BaseEvent {
    @Override public String getId() { return "sand_tomb"; }
    @Override public String getDisplayName() { return "Піщана могила"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 16; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        var world = context.world();
        for (int i = 0; i < 6; i++) {
            BlockPos pos = player.blockPosition().above(8 + i);
            FallingBlockEntity fbe = FallingBlockEntity.fall(world, pos, Blocks.SAND.defaultBlockState());
            fbe.setHurtsEntities(2.0f, 40);
        }
        EventNotifyUtil.notifyPlayer(player, this, "Пісок падає тобі на голову — викопай швидко!");
    }
}
