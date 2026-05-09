package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.util.EventNotifyUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class SharedHealthEvent extends BaseEvent {

    @Override public String getId() { return "coop_shared_health"; }
    @Override public String getDisplayName() { return "Спільне здоров'я"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 2400; }

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> others = CoopUtil.otherPlayers(initiator);
        ServerPlayer partner = others.get(context.random().nextInt(others.size()));

        int endTick = context.server().getTickCount() + getDurationTicks();
        CoopState.SharedHealthSession session = new CoopState.SharedHealthSession(
                initiator.getUUID(), partner.getUUID(),
                endTick, initiator.getHealth(), partner.getHealth());
        CoopState.SHARED_HEALTH.put(initiator.getUUID(), session);
        CoopState.SHARED_HEALTH.put(partner.getUUID(), session);

        initiator.addEffect(new MobEffectInstance(MobEffects.GLOWING, getDurationTicks(), 0, true, false, true));
        partner.addEffect(new MobEffectInstance(MobEffects.GLOWING, getDurationTicks(), 0, true, false, true));

        EventNotifyUtil.notifyAll(initiator, this,
                "Здоров'я " + initiator.getName().getString() + " і "
                        + partner.getName().getString() + " з'єднано на 2 хв (50% relay)");
    }
}
