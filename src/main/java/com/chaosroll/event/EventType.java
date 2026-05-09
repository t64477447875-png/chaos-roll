package com.chaosroll.event;

public enum EventType {
    POSITIVE(0x55FF55),
    NEGATIVE(0xFF5555),
    CHAOTIC(0xFFFF55);

    private final int hudColor;

    EventType(int hudColor) {
        this.hudColor = hudColor;
    }

    public int hudColor() {
        return hudColor;
    }
}