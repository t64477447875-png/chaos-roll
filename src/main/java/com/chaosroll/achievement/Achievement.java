package com.chaosroll.achievement;

public final class Achievement {
    public final String id;
    public final String displayName;
    public final String description;
    public final int targetCount;

    public Achievement(String id, String displayName, String description, int targetCount) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.targetCount = targetCount;
    }
}
