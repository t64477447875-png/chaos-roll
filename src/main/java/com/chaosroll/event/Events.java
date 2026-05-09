package com.chaosroll.event;

import com.chaosroll.event.chaotic.ChaoticEvents;
import com.chaosroll.event.negative.NegativeEvents;
import com.chaosroll.event.positive.PositiveEvents;

public final class Events {

    private Events() {}

    public static void registerAll() {
        PositiveEvents.registerAll();
        NegativeEvents.registerAll();
        ChaoticEvents.registerAll();
    }
}