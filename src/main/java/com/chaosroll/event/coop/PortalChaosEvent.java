package com.chaosroll.event.coop;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventContext;
import com.chaosroll.event.EventRarity;
import com.chaosroll.event.EventType;
import com.chaosroll.event.ScheduledTaskManager;
import com.chaosroll.util.EventNotifyUtil;
import com.chaosroll.util.SafeTeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PortalChaosEvent extends BaseEvent {

    @Override public String getId() { return "coop_portal_chaos"; }
    @Override public String getDisplayName() { return "Порталовий хаос"; }
    @Override public EventType getType() { return EventType.CHAOTIC; }
    @Override public EventRarity getRarity() { return EventRarity.RARE; }
    @Override public int getWeight() { return 10; }
    @Override public int getDurationTicks() { return 1200; }
    @Override public boolean isGlobal() { return true; }

    private static final Map<UUID, Portal> ACTIVE = new HashMap<>();

    private record Portal(UUID owner, ServerLevel level, BlockPos pos, int endTick) {}

    @Override
    public boolean canExecute(EventContext context) {
        return super.canExecute(context) && CoopUtil.hasOtherOnline(context.player());
    }

    @Override
    public void execute(EventContext context) {
        ServerPlayer initiator = context.player();
        List<ServerPlayer> all = CoopUtil.allPlayers(initiator);
        int endTick = context.server().getTickCount() + getDurationTicks();

        for (ServerPlayer p : all) {
            BlockPos near = p.blockPosition().offset(
                    context.random().nextInt(7) - 3,
                    0,
                    context.random().nextInt(7) - 3);
            ACTIVE.put(p.getUUID(), new Portal(p.getUUID(), p.serverLevel(), near, endTick));
        }

        scheduleTick(context.server());

        EventNotifyUtil.notifyAll(initiator, this,
                "Біля кожного гравця з'явився фіолетовий портал на 60с — телепортує до напарника.");
    }

    private static void scheduleTick(MinecraftServer server) {
        ScheduledTaskManager.schedule(server, 5, srv -> {
            tickPortals(srv);
            if (!ACTIVE.isEmpty()) scheduleTick(srv);
        });
    }

    private static void tickPortals(MinecraftServer server) {
        int now = server.getTickCount();
        Iterator<Map.Entry<UUID, Portal>> it = ACTIVE.entrySet().iterator();

        List<Portal> snapshot = new ArrayList<>(ACTIVE.values());

        while (it.hasNext()) {
            Map.Entry<UUID, Portal> e = it.next();
            Portal portal = e.getValue();
            if (now >= portal.endTick) {
                it.remove();
                continue;
            }
            spawnVisuals(portal);
            ServerPlayer entered = findPlayerAtPortal(server, portal);
            if (entered != null) {
                handlePortalEntry(server, entered, portal, snapshot);
                it.remove();
            }
        }
    }

    private static void spawnVisuals(Portal portal) {
        ServerLevel sl = portal.level;
        for (int i = 0; i < 8; i++) {
            double dx = (sl.getRandom().nextDouble() - 0.5) * 1.2;
            double dy = sl.getRandom().nextDouble() * 2.0;
            double dz = (sl.getRandom().nextDouble() - 0.5) * 1.2;
            sl.sendParticles(ParticleTypes.PORTAL,
                    portal.pos.getX() + 0.5 + dx,
                    portal.pos.getY() + dy,
                    portal.pos.getZ() + 0.5 + dz,
                    1, 0, 0, 0, 0);
        }
        sl.sendParticles(ParticleTypes.REVERSE_PORTAL,
                portal.pos.getX() + 0.5,
                portal.pos.getY() + 1.0,
                portal.pos.getZ() + 0.5,
                3, 0.3, 0.3, 0.3, 0.05);
    }

    private static ServerPlayer findPlayerAtPortal(MinecraftServer server, Portal portal) {
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (p.serverLevel() != portal.level) continue;
            double dx = p.getX() - (portal.pos.getX() + 0.5);
            double dy = p.getY() - portal.pos.getY();
            double dz = p.getZ() - (portal.pos.getZ() + 0.5);
            if (dx * dx + dz * dz < 1.5 && Math.abs(dy) < 2.5) return p;
        }
        return null;
    }

    private static void handlePortalEntry(MinecraftServer server, ServerPlayer entered,
                                          Portal entryPortal, List<Portal> snapshot) {
        List<Portal> targets = new ArrayList<>();
        for (Portal p : snapshot) {
            if (!p.owner.equals(entered.getUUID())) targets.add(p);
        }
        if (targets.isEmpty()) {
            SafeTeleportUtil.teleportRandom(entered, 30);
            entered.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "[Chaos Roll] Інших порталів немає — телепортовано рандомно."));
            return;
        }
        Portal dest = targets.get(server.overworld().getRandom().nextInt(targets.size()));
        ServerPlayer partner = server.getPlayerList().getPlayer(dest.owner);
        if (partner == null) {
            entered.teleportTo(dest.pos.getX() + 0.5, dest.pos.getY() + 1, dest.pos.getZ() + 0.5);
            return;
        }
        if (partner.serverLevel() != entered.serverLevel()) {
            entered.teleportTo(dest.pos.getX() + 0.5, dest.pos.getY() + 1, dest.pos.getZ() + 0.5);
            return;
        }
        double angle = server.overworld().getRandom().nextDouble() * Math.PI * 2.0;
        double radius = 6.0;
        double tx = partner.getX() + Math.cos(angle) * radius;
        double tz = partner.getZ() + Math.sin(angle) * radius;
        entered.teleportTo(tx, partner.getY(), tz);
        server.getPlayerList().broadcastSystemMessage(
                net.minecraft.network.chat.Component.literal(
                        "[Chaos Roll] " + entered.getName().getString()
                                + " пройшов крізь портал до " + partner.getName().getString() + "!"),
                false);
    }
}
