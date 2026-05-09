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

public class TwinFateEvent extends BaseEvent {

    @Override public String getId() { return "coop_twin_fate"; }
    @Override public String getDisplayName() { return "Дві долі"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 6000; }

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
        CoopState.TwinFateSession session = new CoopState.TwinFateSession(
                initiator.getUUID(), partner.getUUID(), endTick);
        CoopState.TWIN_FATE.put(initiator.getUUID(), session);
        CoopState.TWIN_FATE.put(partner.getUUID(), session);

        initiator.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, true, false, true));
        partner.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, true, false, true));

        EventNotifyUtil.notifyAll(initiator, this,
                "Долі " + initiator.getName().getString() + " і "
                        + partner.getName().getString() + " переплелися на 5 хв.");
    }

    /**
     * Called from the roll-result pipeline whenever a player receives a buff via Chaos Roll.
     * If the player is part of an active twin-fate session, mirror the effect (half duration)
     * to their partner.
     */
    public static void onRollEffect(ServerPlayer source, MobEffectInstance effect) {
        if (effect == null || effect.getEffect() == null) return;
        CoopState.TwinFateSession session = CoopState.TWIN_FATE.get(source.getUUID());
        if (session == null) return;
        if (source.getServer() == null) return;
        if (source.getServer().getTickCount() >= session.endTick) return;
        java.util.UUID otherId = session.a.equals(source.getUUID()) ? session.b : session.a;
        ServerPlayer partner = source.getServer().getPlayerList().getPlayer(otherId);
        if (partner == null) return;
        int halfDur = Math.max(20, effect.getDuration() / 2);
        partner.addEffect(new MobEffectInstance(effect.getEffect(), halfDur, effect.getAmplifier()));
    }
}
