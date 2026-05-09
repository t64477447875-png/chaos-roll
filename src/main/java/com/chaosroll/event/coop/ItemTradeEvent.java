package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemTradeEvent extends BaseEvent {

    @Override public String getId() { return "coop_item_trade"; }
    @Override public String getDisplayName() { return "Обмін руками"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.COMMON; }
    @Override public int getWeight() { return 25; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> others = CoopUtil.otherPlayers(initiator);
        if (others.isEmpty()) return;
        ServerPlayer partner = others.get(context.random().nextInt(others.size()));

        Inventory ia = initiator.getInventory();
        Inventory ib = partner.getInventory();
        ItemStack a = ia.getSelected().copy();
        ItemStack b = ib.getSelected().copy();
        ia.items.set(ia.selected, b);
        ib.items.set(ib.selected, a);

        EventNotifyUtil.notifyAll(initiator, this,
                initiator.getName().getString() + " і " + partner.getName().getString()
                        + " обмінялись предметами в руці!");
    }
}
