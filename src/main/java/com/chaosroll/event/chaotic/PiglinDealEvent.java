package com.chaosroll.event.chaotic;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.InventoryUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PiglinDealEvent extends BaseEvent {
    @Override public String getId() { return "piglin_deal"; }
    @Override public String getDisplayName() { return "Угода з піглінами"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 18; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        InventoryUtil.giveOrDrop(player, new ItemStack(Items.GOLD_INGOT, 16));
        for (int i = 0; i < 3; i++) {
            Piglin p = EntityType.PIGLIN.create(context.world());
            if (p == null) continue;
            double angle = (Math.PI * 2 * i) / 3.0;
            double r = 4;
            p.setPos(player.getX() + Math.cos(angle) * r, player.getY(),
                    player.getZ() + Math.sin(angle) * r);
            p.setImmuneToZombification(true);
            p.setPersistenceRequired();
            context.world().addFreshEntity(p);
        }
        EventNotifyUtil.notifyPlayer(player, this, "+16 золота + 3 розлючені пігліни!");
    }
}
