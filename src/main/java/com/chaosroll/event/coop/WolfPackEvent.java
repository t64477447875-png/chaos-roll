package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;

import java.util.List;

public class WolfPackEvent extends BaseEvent {

    @Override public String getId() { return "coop_wolf_pack"; }
    @Override public String getDisplayName() { return "Зграя вовків"; }
    @Override public EventType getType() { return EventType.POSITIVE; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 12; }
    @Override public boolean isGlobal() { return true; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> all = CoopUtil.allPlayers(initiator);
        if (all.isEmpty()) return;

        double cx = 0, cy = 0, cz = 0;
        for (ServerPlayer p : all) {
            cx += p.getX();
            cy += p.getY();
            cz += p.getZ();
        }
        cx /= all.size();
        cy /= all.size();
        cz /= all.size();

        for (int i = 0; i < 4; i++) {
            Wolf wolf = EntityType.WOLF.create(context.world());
            if (wolf == null) continue;
            double dx = (context.random().nextDouble() - 0.5) * 4;
            double dz = (context.random().nextDouble() - 0.5) * 4;
            wolf.setPos(cx + dx, cy, cz + dz);
            ServerPlayer ownerFor = all.get(i % all.size());
            wolf.tame(ownerFor);
            wolf.setHealth(wolf.getMaxHealth());
            wolf.setCustomName(net.minecraft.network.chat.Component.literal("Хаос-вовк"));
            context.world().addFreshEntity(wolf);
        }
        EventNotifyUtil.notifyAll(initiator, this,
                "4 прирученими вовками з'явились між гравцями. Атакуйте, вони допоможуть!");
    }
}
