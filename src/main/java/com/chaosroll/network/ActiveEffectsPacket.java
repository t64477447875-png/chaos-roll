package com.chaosroll.network;

import com.chaosroll.ChaosRollMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record ActiveEffectsPacket(List<Entry> entries) implements CustomPacketPayload {

    public record Entry(String displayName, int typeOrdinal, int secondsRemaining) {}

    public static final CustomPacketPayload.Type<ActiveEffectsPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ChaosRollMod.MOD_ID, "active_effects")
            );

    public static final StreamCodec<FriendlyByteBuf, ActiveEffectsPacket> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public ActiveEffectsPacket decode(FriendlyByteBuf buf) {
                    int n = buf.readVarInt();
                    List<Entry> list = new ArrayList<>(n);
                    for (int i = 0; i < n; i++) {
                        String displayName = buf.readUtf();
                        int typeOrdinal = buf.readVarInt();
                        int secondsRemaining = buf.readVarInt();
                        list.add(new Entry(displayName, typeOrdinal, secondsRemaining));
                    }
                    return new ActiveEffectsPacket(list);
                }

                @Override
                public void encode(FriendlyByteBuf buf, ActiveEffectsPacket pkt) {
                    buf.writeVarInt(pkt.entries.size());
                    for (Entry e : pkt.entries) {
                        buf.writeUtf(e.displayName());
                        buf.writeVarInt(e.typeOrdinal());
                        buf.writeVarInt(e.secondsRemaining());
                    }
                }
            };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
