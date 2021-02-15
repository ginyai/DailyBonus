package dev.ginyai.dailybonus.time;

import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.api.time.TimeRange;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

public class SimpleTimeService implements DailyBonusTimeService {
    private ZoneId timeZone;
    private LocalTime startOfDay;
    private DayOfWeek startOfWeek;

    public SimpleTimeService(ZoneId timeZone, LocalTime startOfDay, DayOfWeek startOfWeek) {
        this.timeZone = timeZone;
        this.startOfDay = startOfDay;
        this.startOfWeek = startOfWeek;
    }

    public void updateSettings(ZoneId timeZone, LocalTime startOfDay, DayOfWeek startOfWeek) {
        this.timeZone = timeZone;
        this.startOfDay = startOfDay;
        this.startOfWeek = startOfWeek;
    }

    @Override
    public ZoneId getTimeZone() {
        return timeZone;
    }

    @Override
    public LocalTime getStartOfDay() {
        return startOfDay;
    }

    @Override
    public DayOfWeek getStartOfWeek() {
        return startOfWeek;
    }

    @Override
    public LocalDate getDate(Instant instant) {
        return getDate(ZonedDateTime.ofInstant(instant, timeZone).toLocalDateTime());
    }

    @Override
    public LocalDate getDate(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        if (time.isAfter(startOfDay)) {
            return dateTime.toLocalDate();
        } else {
            return dateTime.toLocalDate().plusDays(-1);
        }
    }

    @Override
    public TimeRange<LocalDateTime> getCycleAt(TimeCycle cycle, LocalDateTime at) {
        LocalDateTime start;
        LocalDateTime end;
        LocalDate date = getDate(at);
        switch (cycle) {
            case ONCE:
                start = LocalDateTime.MIN;
                end = LocalDateTime.MAX;
                break;
            case DAY:
                start = date.atTime(getStartOfDay());
                end = start.plusDays(1);
                break;
            case WEEK:
                start = date.with(ChronoField.DAY_OF_WEEK, getStartOfWeek().getValue()).atTime(getStartOfDay());
                end = start.plusDays(7);
                break;
            case MONTH:
                start = date.withDayOfMonth(1).atTime(getStartOfDay());
                end = date.plusMonths(1).withDayOfMonth(1).atTime(getStartOfDay());
                break;
            case YEAR:
                start = date.withDayOfYear(1).atTime(getStartOfDay());
                end = date.plusYears(1).withDayOfYear(1).atTime(getStartOfDay());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported cycle type: " + cycle);
        }
        return new TimeRange<>(start, end);
    }

    @Override
    public Instant toInstance(LocalDateTime dateTime) {
        return dateTime.atZone(timeZone).toInstant();
    }

}
