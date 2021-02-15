package dev.ginyai.dailybonus.placeholder;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;
import dev.ginyai.dailybonus.DailyBonusMain;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class TimeValueParser implements IPlaceholderParser<Long> {
    //todo: set formatter by config
    private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendValue(ChronoField.EPOCH_DAY)
        .appendLiteral("日")
        .appendValue(ChronoField.HOUR_OF_DAY)
        .appendLiteral("时")
        .appendValue(ChronoField.MINUTE_OF_HOUR)
        .appendLiteral("分")
        .toFormatter();
    private static Map<String, DateTimeFormatter> formatterMap;
    private static Map<String, TemporalField> idToFieldMap;

    static {
        formatterMap = ImmutableMap.of(
            "default", formatter,
            "min", new DateTimeFormatterBuilder().appendValue(ChronoField.MINUTE_OF_DAY).appendLiteral("分").toFormatter()
        );
        LinkedHashMap<String, TemporalField> map = new LinkedHashMap<>();
        for (ChronoField field: ChronoField.values()) {
//            map.put(field.name().toLowerCase(Locale.ROOT), field);
            map.put(field.toString().toLowerCase(Locale.ROOT), field);
        }
        idToFieldMap = ImmutableMap.copyOf(map);
    }

    private DailyBonusMain dailyBonus;

    public TimeValueParser(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    @Nullable
    @Override
    public Object parsePlaceholder(Long epochMilli, String... args) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.ofHours(0));
        if (args.length == 0) {
            return zonedDateTime.format(formatter);
        } else if (args.length == 1) {
            //todo: format by settings.
            String key = args[0].toLowerCase(Locale.ROOT);
            DateTimeFormatter formatter = formatterMap.get(key);
            if (formatter != null) {
                return zonedDateTime.format(formatter);
            }
            TemporalField field = idToFieldMap.get(key);
            if (field != null) {
                return field.getFrom(zonedDateTime);
            }
        }
        return null;
    }

    @Override
    public void visitData(Long epochMilli, IPlaceholderContainer.PlaceholderVisitor visitor) {
        //todo: format by settings.
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.ofHours(0));
        idToFieldMap.forEach((k, v) -> visitor.visit(k, v.getFrom(dateTime)));
        formatterMap.forEach((k, v) -> visitor.visit(k, dateTime.format(v)));
    }
}
