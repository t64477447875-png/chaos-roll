package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RollRequestPacket() implements CustomPacketPayload {

    public static final RollRequestPacket INSTANCE = new RollRequestPacket();

    public static final CustomPacketPayload.Type<RollRequestPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "roll_request")
            );

    public static final StreamCodec<FriendlyByteBuf, RollRequestPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}