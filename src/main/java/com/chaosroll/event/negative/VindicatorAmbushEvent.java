package com.chaosroll.event.negative;

import com.chaosroll.event.*;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VindicatorAmbushEvent extends BaseEvent {
    @Override public String getId() { return "vindicator_ambush"; }
    @Override public String getDisplayName() { return "Засада грабіжників"; }
    @Override public EventType getType() { return EventType.NEGATIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 22; }

    @Override
    public void execute(EventContext context) {
        var player = context.player();
        for (int i = 0; i < 3; i++) {
            Vindicator v = EntityType.VINDICATOR.create(context.world());
            if (v == null) continue;
            double angle = (Math.PI * 2 * i) / 3.0;
            double r = 5;
            v.setPos(player.getX() + Math.cos(angle) * r, player.getY(),
                    player.getZ() + Math.sin(angle) * r);
            v.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
            v.setTarget(player);
            v.setPersistenceRequired();
            context.world().addFreshEntity(v);
        }
        EventNotifyUtil.notifyPlayer(player, this, "3 ваймайстри з сокирами!");
    }
}
