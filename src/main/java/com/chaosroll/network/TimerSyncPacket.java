package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TimerSyncPacket(int secondsRemaining, boolean rollReady) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TimerSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "timer_sync")
            );

    public static final StreamCodec<FriendlyByteBuf, TimerSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, TimerSyncPacket::secondsRemaining,
                    ByteBufCodecs.BOOL, TimerSyncPacket::rollReady,
                    TimerSyncPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}