package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GlobalEventPacket(
        String initiatorName,
        String eventId,
        String displayName,
        int typeOrdinal,
        int rarityOrdinal
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GlobalEventPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "global_event")
            );

    public static final StreamCodec<FriendlyByteBuf, GlobalEventPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, GlobalEventPacket::initiatorName,
                    ByteBufCodecs.STRING_UTF8, GlobalEventPacket::eventId,
                    ByteBufCodecs.STRING_UTF8, GlobalEventPacket::displayName,
                    ByteBufCodecs.VAR_INT, GlobalEventPacket::typeOrdinal,
                    ByteBufCodecs.VAR_INT, GlobalEventPacket::rarityOrdinal,
                    GlobalEventPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
