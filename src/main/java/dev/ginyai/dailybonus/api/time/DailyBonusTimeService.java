package dev.ginyai.dailybonus.api.time;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public interface DailyBonusTimeService {

    default LocalDate getToday() {
        return getDate(Instant.now());
    }

    ZoneId getTimeZone();

    LocalTime getStartOfDay();

    DayOfWeek getStartOfWeek();

    LocalDate getDate(Instant instant);

    LocalDate getDate(LocalDateTime dateTime);

    default TimeRange<LocalDateTime> getCurrentCycle(TimeCycle timeCycle) {
        return getCycleAt(timeCycle, LocalDateTime.now());
    }

    TimeRange<LocalDateTime> getCycleAt(TimeCycle cycle, LocalDateTime at);

    Instant toInstance(LocalDateTime dateTime);

}
