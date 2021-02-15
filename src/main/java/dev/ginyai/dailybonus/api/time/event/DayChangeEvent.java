package dev.ginyai.dailybonus.api.time.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class DayChangeEvent extends AbstractEvent {
    private Cause cause;

    public DayChangeEvent(Cause cause) {
        this.cause = cause;
    }

    @Override
    public Cause getCause() {
        return null;
    }
}
