package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventorySwapEvent extends BaseEvent {

    @Override public String getId() { return "coop_inventory_swap"; }
    @Override public String getDisplayName() { return "Обмін інвентарями"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 8; }
    @Override public int getDurationTicks() { return 1800; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> others = CoopUtil.otherPlayers(initiator);
        ServerPlayer partner = others.get(context.random().nextInt(others.size()));

        List<ItemStack> snapshotA = snapshot(initiator.getInventory());
        List<ItemStack> snapshotB = snapshot(partner.getInventory());

        load(initiator.getInventory(), snapshotB);
        load(partner.getInventory(), snapshotA);

        UUID idA = initiator.getUUID();
        UUID idB = partner.getUUID();

        ScheduledTaskManager.schedule(context.server(), getDurationTicks(), srv -> {
            ServerPlayer pa = srv.getPlayerList().getPlayer(idA);
            ServerPlayer pb = srv.getPlayerList().getPlayer(idB);
            if (pa == null || pb == null) return;
            List<ItemStack> currentA = snapshot(pa.getInventory());
            List<ItemStack> currentB = snapshot(pb.getInventory());
            load(pa.getInventory(), currentB);
            load(pb.getInventory(), currentA);
            pa.sendSystemMessage(Component.literal("[Chaos Roll] Інвентар повернуто."));
            pb.sendSystemMessage(Component.literal("[Chaos Roll] Інвентар повернуто."));
        });

        EventNotifyUtil.notifyAll(initiator, this,
                "Інвентарі " + initiator.getName().getString() + " і "
                        + partner.getName().getString() + " обміняні на 90с!");
    }

    private static List<ItemStack> snapshot(Inventory inv) {
        List<ItemStack> result = new ArrayList<>(inv.getContainerSize());
        for (int i = 0; i < inv.getContainerSize(); i++) {
            result.add(inv.getItem(i).copy());
        }
        return result;
    }

    private static void load(Inventory inv, List<ItemStack> snapshot) {
        int n = Math.min(inv.getContainerSize(), snapshot.size());
        for (int i = 0; i < n; i++) {
            inv.setItem(i, snapshot.get(i).copy());
        }
    }
}
