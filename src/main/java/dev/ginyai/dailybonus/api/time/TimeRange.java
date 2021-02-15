package dev.ginyai.dailybonus.api.time;

import com.google.common.base.Preconditions;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

public final class TimeRange<T extends Comparable<? super T> & Temporal & TemporalAdjuster> {
    private final T start;
    private final T end;

    public TimeRange(T start, T end) {
        this.start = start;
        this.end = end;
        Preconditions.checkState(start.compareTo(end) <= 0);
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    public boolean isIn(T t) {
        return start.compareTo(t) <= 0 && end.compareTo(t) >= 0;
    }
}
