package com.chaosroll.util;

import com.chaosroll.config.ChaosRollConfig;
import com.chaosroll.config.ConfigManager;
import com.chaosroll.event.BaseEvent;
import com.chaosroll.event.EventType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public final class WeightedRandom {

    private WeightedRandom() {}

    public static BaseEvent pick(List<BaseEvent> events, RandomSource random) {
        return pick(events, random, null);
    }

    public static BaseEvent pick(List<BaseEvent> events, RandomSource random, ServerPlayer player) {
        if (events.isEmpty()) {
            return null;
        }
        long totalWeight = 0;
        for (BaseEvent e : events) {
            totalWeight += effectiveWeight(e, player);
        }
        if (totalWeight <= 0) return events.get(0);
        long roll = (long) (random.nextDouble() * totalWeight);
        long cum = 0;
        for (BaseEvent e : events) {
            cum += effectiveWeight(e, player);
            if (roll < cum) {
                return e;
            }
        }
        return events.get(events.size() - 1);
    }

    public static int effectiveWeight(BaseEvent e) {
        return effectiveWeight(e, null);
    }

    public static int effectiveWeight(BaseEvent e, ServerPlayer player) {
        int raw = Math.max(1, e.getWeight());
        double mul = typeMultiplier(e.getType()) * dimensionMultiplier(e.getType(), player);
        return Math.max(1, (int) Math.round(raw * mul));
    }

    public static double typeMultiplier(EventType type) {
        ChaosRollConfig c = ConfigManager.get();
        double pos;
        double neg;
        double cha;
        switch (c.balanceMode == null ? "balanced" : c.balanceMode) {
            case "more_positive" -> { pos = 70; neg = 20; cha = 10; }
            case "more_negative" -> { pos = 20; neg = 60; cha = 20; }
            case "pure_chaos"    -> { pos = 25; neg = 25; cha = 50; }
            default              -> { pos = Math.max(0, c.positiveWeight);
                                       neg = Math.max(0, c.negativeWeight);
                                       cha = Math.max(0, c.chaoticWeight); }
        }
        double total = pos + neg + cha;
        if (total <= 0) return 1.0;
        double base = switch (type) {
            case POSITIVE -> pos;
            case NEGATIVE -> neg;
            case CHAOTIC  -> cha;
        };
        return (base / total) * 3.0;
    }

    public static double dimensionMultiplier(EventType type, ServerPlayer player) {
        if (player == null) return 1.0;
        ChaosRollConfig c = ConfigManager.get();
        if (!c.enableDifficultyScaling) return 1.0;
        ResourceKey<Level> dim = player.serverLevel().dimension();
        if (dim == Level.NETHER) {
            return switch (type) {
                case POSITIVE -> 0.7;
                case NEGATIVE -> 1.5;
                case CHAOTIC  -> 1.0;
            };
        }
        if (dim == Level.END) {
            return switch (type) {
                case POSITIVE -> 0.5;
                case NEGATIVE -> 2.0;
                case CHAOTIC  -> 1.0;
            };
        }
        return 1.0;
    }
}
