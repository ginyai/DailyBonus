package dev.ginyai.dailybonus.placeholder;

import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;

import javax.annotation.Nullable;

public interface IPlaceholderParser<T> {
    @Nullable
    Object parsePlaceholder(T t, String... args);

    void visitData(T t, IPlaceholderContainer.PlaceholderVisitor visitor);
}
