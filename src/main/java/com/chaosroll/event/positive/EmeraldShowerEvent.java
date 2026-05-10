package com.chaosroll.event.positive;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Drops 32 emeralds in the air around the player. Free trade currency.
 */
public class EmeraldShowerEvent extends BaseEvent {
    @Override public String getId() { return "emerald_shower"; }
    @Override public String getDisplayName() { return "Дощ із смарагдів"; }
    @Override public String getDescription() { return "32 смарагди падають з неба прямо в твої руки."; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }

    @Override
    public void execute(EventContext context) {
        var p = context.player();
        var world = context.world();
        var rng = context.random();
        for (int i = 0; i < 32; i++) {
            double dx = (rng.nextDouble() - 0.5) * 4;
            double dz = (rng.nextDouble() - 0.5) * 4;
            ItemEntity ie = new ItemEntity(world,
                    p.getX() + dx, p.getY() + 5, p.getZ() + dz,
                    new ItemStack(Items.EMERALD, 1));
            ie.setDeltaMovement(0, -0.1, 0);
            world.addFreshEntity(ie);
        }
        EventNotifyUtil.notifyPlayer(p, this, "32 смарагди падають з неба!");
    }
}
