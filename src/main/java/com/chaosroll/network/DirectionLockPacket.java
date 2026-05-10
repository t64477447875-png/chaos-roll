package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C packet that tells the client to lock its yaw to {@code lockedYaw} for the given duration.
 * The packet's pitch axis is unaffected and movement (W/A/S/D) is always allowed.
 */
public record DirectionLockPacket(int durationTicks, float lockedYaw) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DirectionLockPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "direction_lock"));

    public static final StreamCodec<FriendlyByteBuf, DirectionLockPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, DirectionLockPacket::durationTicks,
                    ByteBufCodecs.FLOAT, DirectionLockPacket::lockedYaw,
                    DirectionLockPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
