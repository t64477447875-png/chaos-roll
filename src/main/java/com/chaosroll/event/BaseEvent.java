package com.chaosroll.event;

public abstract class BaseEvent {

    public abstract String getId();

    public abstract String getDisplayName();

    public abstract EventType getType();

    public abstract EventRarity getRarity();

    public abstract int getWeight();

    public abstract void execute(EventContext context);

    public boolean canExecute(EventContext context) {
        return context.player() != null
                && context.player().isAlive()
                && context.world() != null;
    }

    public int getDurationTicks() {
        return 0;
    }

    public boolean isGlobal() {
        return false;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEvent other)) return false;
        return getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return getId().hashCode();
    }
}