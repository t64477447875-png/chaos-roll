package com.chaosroll.client.hud;

public final class GlobalBannerState {

    public static final int LIFETIME_TICKS = 100;

    private static String initiatorName = "";
    private static String displayName = "";
    private static int typeOrdinal = 0;
    private static int ticksLeft = 0;

    private GlobalBannerState() {}

    public static void show(String initiator, String name, int type) {
        initiatorName = initiator == null ? "" : initiator;
        displayName = name == null ? "" : name;
        typeOrdinal = type;
        ticksLeft = LIFETIME_TICKS;
    }

    public static void clientTick() {
        if (ticksLeft > 0) ticksLeft--;
    }

    public static boolean isVisible() {
        return ticksLeft > 0;
    }

    public static String getInitiatorName() { return initiatorName; }
    public static String getDisplayName() { return displayName; }
    public static int getTypeOrdinal() { return typeOrdinal; }
    public static int getTicksLeft() { return ticksLeft; }
}
