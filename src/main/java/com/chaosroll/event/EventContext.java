package com.chaosroll.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

public record EventContext(
        MinecraftServer server,
        ServerPlayer player,
        ServerLevel world,
        RandomSource random
) {
    public static EventContext forPlayer(ServerPlayer player) {
        return new EventContext(
                player.getServer(),
                player,
                player.serverLevel(),
                player.getRandom()
        );
    }
}