package com.chaosroll.client.state;

public final class NoJumpState {

    private static long endTimeMs = 0L;

    private NoJumpState() {}

    public static void start(int durationTicks) {
        endTimeMs = System.currentTimeMillis() + durationTicks * 50L;
    }

    public static void clear() {
        endTimeMs = 0L;
    }

    public static boolean isActive() {
        return System.currentTimeMillis() < endTimeMs;
    }
}
