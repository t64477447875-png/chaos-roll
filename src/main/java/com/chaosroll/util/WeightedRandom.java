package com.chaosroll.util;

import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventType;
import net.minecraft.util.RandomSource;

import java.util.List;

public final class WeightedRandom {

    private WeightedRandom() {}

    public static BaseEvent pick(List<BaseEvent> events, RandomSource random) {
        if (events.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (BaseEvent e : events) {
            totalWeight += effectiveWeight(e);
        }
        int roll = random.nextInt(totalWeight);
        int cum = 0;
        for (BaseEvent e : events) {
            cum += effectiveWeight(e);
            if (roll < cum) {
                return e;
            }
        }
        return events.get(events.size() - 1);
    }

    private static int effectiveWeight(BaseEvent e) {
        int w = Math.max(1, e.getWeight());
        if (e.getType() == EventType.CHAOTIC) {
            return Math.max(1, w + w / 2);
        }
        return w;
    }
}