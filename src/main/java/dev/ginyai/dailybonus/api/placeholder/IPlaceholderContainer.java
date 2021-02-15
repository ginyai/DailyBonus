package dev.ginyai.dailybonus.api.placeholder;

import javax.annotation.Nullable;

public interface IPlaceholderContainer {
    @Nullable
    Object parsePlaceholder(String... args);

    void visitData(PlaceholderVisitor visitor);

    interface PlaceholderVisitor {
        void visit(String key, Object value);
    }
}
