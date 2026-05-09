package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RollResultPacket(
        String eventId,
        String displayName,
        int typeOrdinal,
        int rarityOrdinal
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RollResultPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "roll_result")
            );

    public static final StreamCodec<FriendlyByteBuf, RollResultPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, RollResultPacket::eventId,
                    ByteBufCodecs.STRING_UTF8, RollResultPacket::displayName,
                    ByteBufCodecs.VAR_INT, RollResultPacket::typeOrdinal,
                    ByteBufCodecs.VAR_INT, RollResultPacket::rarityOrdinal,
                    RollResultPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
