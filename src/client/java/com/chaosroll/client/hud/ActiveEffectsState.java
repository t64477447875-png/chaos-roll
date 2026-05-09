package com.chaosroll.client.hud;

import com.chaosroll.network.ActiveEffectsPacket;

import java.util.ArrayList;
import java.util.List;

public final class ActiveEffectsState {

    private static List<ActiveEffectsPacket.Entry> entries = new ArrayList<>();

    private ActiveEffectsState() {}

    public static void update(List<ActiveEffectsPacket.Entry> incoming) {
        entries = incoming == null ? new ArrayList<>() : new ArrayList<>(incoming);
    }

    public static List<ActiveEffectsPacket.Entry> getEntries() {
        return entries;
    }
}
