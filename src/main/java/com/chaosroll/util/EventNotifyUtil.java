package com.chaosroll.util;

import com.chaosroll.event.BaseEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class EventNotifyUtil {

    private EventNotifyUtil() {}

    public static void notifyPlayer(ServerPlayer player, BaseEvent event, String message) {
        player.sendSystemMessage(buildMessage(event, message, null));
    }

    public static void notifyAll(ServerPlayer triggering, BaseEvent event, String message) {
        MinecraftServer server = triggering.getServer();
        if (server == null) return;
        server.getPlayerList().broadcastSystemMessage(buildMessage(event, message, triggering.getName().getString()), false);
    }

    private static Component buildMessage(BaseEvent event, String message, String triggering) {
        ChatFormatting color = switch (event.getType()) {
            case POSITIVE -> ChatFormatting.GREEN;
            case NEGATIVE -> ChatFormatting.RED;
            case CHAOTIC -> ChatFormatting.YELLOW;
        };
        var prefix = Component.literal("[Chaos Roll] ").withStyle(ChatFormatting.GOLD);
        if (triggering != null) {
            prefix.append(Component.literal(triggering).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" → ").withStyle(ChatFormatting.GRAY));
        }
        return prefix
                .append(Component.literal(event.getDisplayName()).withStyle(color))
                .append(Component.literal(" — " + message).withStyle(ChatFormatting.WHITE));
    }
}