package dev.ginyai.dailybonus.util;

import java.lang.reflect.Field;

public final class ReflectHelper {

    @SuppressWarnings("unchecked")
    public static <T, V> V getPrivateValue(Class<T> tClass, T t, String field) {
        try {
            Field field1 = tClass.getDeclaredField(field);
            field1.setAccessible(true);
            return (V) field1.get(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, V> void setPrivateValue(Class<T> tClass, T t, String field, V value) {
        try {
            Field field1 = tClass.getDeclaredField(field);
            field1.setAccessible(true);
            field1.set(t, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
