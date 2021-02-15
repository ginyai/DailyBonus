package dev.ginyai.dailybonus.util;

import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class UtilMethods {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T)o;
    }

    public static List<String> startWith(String prefix, String... strings) {
        return startWith(prefix, Arrays.asList(strings));
    }

    public static List<String> startWith(String prefix, Collection<String> strings) {
        return startWith(prefix, strings.stream());
    }

    public static List<String> startWith(String prefix, Stream<String> strings) {
        String p = prefix.toLowerCase(Locale.ROOT);
        return strings.filter(s -> s.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList());
    }

    public static <T> BiConsumer<T, Throwable> handleException(Logger logger, String errorMessage) {
        return (t, throwable) -> {
            if (throwable != null) {
                logger.error(errorMessage, throwable);
            }
        };
    }
}
