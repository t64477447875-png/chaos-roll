package com.chaosroll.client.hud;

public final class TimerState {

    private static int secondsRemaining = 60;
    private static boolean rollReady = false;

    private TimerState() {}

    public static void update(int seconds, boolean ready) {
        secondsRemaining = seconds;
        rollReady = ready;
    }

    public static int getSecondsRemaining() {
        return secondsRemaining;
    }

    public static boolean isRollReady() {
        return rollReady;
    }
}