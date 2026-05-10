package com.chaosroll.client.state;

public final class ScreenFlipState {

    private static long endTimeMs = 0L;

    private ScreenFlipState() {}

    public static void start(int durationTicks) {
        long durationMs = durationTicks * 50L;
        endTimeMs = System.currentTimeMillis() + durationMs;
    }

    public static void clear() {
        endTimeMs = 0L;
    }

    public static boolean isActive() {
        return System.currentTimeMillis() < endTimeMs;
    }
}
