package com.chaosroll.event.coop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class CoopUtil {

    private CoopUtil() {}

    public static List<ServerPlayer> otherPlayers(ServerPlayer self) {
        MinecraftServer server = self.getServer();
        if (server == null) return List.of();
        List<ServerPlayer> result = new ArrayList<>();
        UUID selfId = self.getUUID();
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (!p.getUUID().equals(selfId)) result.add(p);
        }
        return result;
    }

    public static List<ServerPlayer> allPlayers(ServerPlayer self) {
        MinecraftServer server = self.getServer();
        if (server == null) return List.of();
        return new ArrayList<>(server.getPlayerList().getPlayers());
    }

    public static List<ServerPlayer> sample(List<ServerPlayer> players, int n, java.util.random.RandomGenerator rng) {
        List<ServerPlayer> copy = new ArrayList<>(players);
        Collections.shuffle(copy, new java.util.Random(rng.nextLong()));
        if (copy.size() > n) {
            return copy.subList(0, n);
        }
        return copy;
    }

    public static boolean hasOtherOnline(ServerPlayer self) {
        return !otherPlayers(self).isEmpty();
    }
}
