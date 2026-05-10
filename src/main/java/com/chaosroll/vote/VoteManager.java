package com.chaosroll.vote;

import com.chaosroll.config.ConfigManager;
import com.chaosroll.event.BaseEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks vote-to-skip for a currently rolling event.
 * Only one active vote at a time (per server). Auto-resets after each roll.
 */
public final class VoteManager {

    private static UUID rollingPlayerId;
    private static String rollingEventId;
    private static String rollingEventName;
    private static final Set<UUID> votedYes = new HashSet<>();
    private static final Set<UUID> votedNo = new HashSet<>();

    private VoteManager() {}

    public static synchronized void startVote(ServerPlayer initiator, BaseEvent event) {
        if (!ConfigManager.get().voteToSkipEnabled) return;
        rollingPlayerId = initiator.getUUID();
        rollingEventId = event.getId();
        rollingEventName = event.getDisplayName();
        votedYes.clear();
        votedNo.clear();
        MinecraftServer server = initiator.getServer();
        if (server == null) return;
        Component msg = Component.literal(
                "§6[Chaos Roll] §fГолосування: пропустити '" + event.getDisplayName() +
                        "' для §a" + initiator.getName().getString() + "§f? "
                        + "§a/chaosroll vote yes§f / §c/chaosroll vote no");
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            p.sendSystemMessage(msg);
        }
    }

    public static synchronized boolean hasActiveVote() {
        return rollingPlayerId != null;
    }

    public static synchronized String currentEventId() {
        return rollingEventId;
    }

    public static synchronized String currentEventName() {
        return rollingEventName;
    }

    public static synchronized boolean castVote(ServerPlayer voter, boolean yes) {
        if (rollingPlayerId == null) return false;
        UUID id = voter.getUUID();
        if (yes) {
            votedYes.add(id);
            votedNo.remove(id);
        } else {
            votedNo.add(id);
            votedYes.remove(id);
        }
        return true;
    }

    public static synchronized boolean shouldSkip(MinecraftServer server) {
        if (rollingPlayerId == null) return false;
        int total = server.getPlayerList().getPlayers().size();
        if (total <= 1) return false;
        int yes = votedYes.size();
        return yes * 2 > total;
    }

    public static synchronized void clearVote() {
        rollingPlayerId = null;
        rollingEventId = null;
        rollingEventName = null;
        votedYes.clear();
        votedNo.clear();
    }

    public static synchronized int getYesCount() { return votedYes.size(); }
    public static synchronized int getNoCount() { return votedNo.size(); }
}
