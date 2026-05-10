package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C packet that switches the local player's camera into "morph" mode for the given duration.
 * In morph mode the client switches into third-person view, hides the first-person hand,
 * and adjusts eye height to roughly match the morph target.
 *
 * <p>{@code mobTypeKey} is the entity type registry key (e.g. {@code minecraft:zombie}).
 * Use {@code durationTicks <= 0} to immediately clear morph state.</p>
 */
public record MorphPacket(int durationTicks, String mobTypeKey, float eyeHeight) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MorphPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "mob_morph"));

    public static final StreamCodec<FriendlyByteBuf, MorphPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, MorphPacket::durationTicks,
                    ByteBufCodecs.STRING_UTF8, MorphPacket::mobTypeKey,
                    ByteBufCodecs.FLOAT, MorphPacket::eyeHeight,
                    MorphPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
