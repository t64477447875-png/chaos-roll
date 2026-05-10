package com.chaosroll.client.state;

public final class DirectionLockState {

    private static long endTimeMs = 0L;
    private static float lockedYaw = 0.0f;

    private DirectionLockState() {}

    public static void start(int durationTicks, float yaw) {
        endTimeMs = System.currentTimeMillis() + durationTicks * 50L;
        lockedYaw = yaw;
    }

    public static void clear() {
        endTimeMs = 0L;
    }

    public static boolean isActive() {
        return System.currentTimeMillis() < endTimeMs;
    }

    public static float lockedYaw() {
        return lockedYaw;
    }
}
