package dev.ginyai.dailybonus.placeholder;

import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class WarpedParer<T> implements IPlaceholderContainer {
    private final IPlaceholderParser<T> placeholderParser;
    private final Supplier<T> tSupplier;

    public WarpedParer(IPlaceholderParser<T> placeholderParser, Supplier<T> tSupplier) {
        this.placeholderParser = placeholderParser;
        this.tSupplier = tSupplier;
    }

    @Nullable
    @Override
    public Object parsePlaceholder(String... args) {
        return placeholderParser.parsePlaceholder(tSupplier.get(), args);
    }

    @Override
    public void visitData(PlaceholderVisitor visitor) {
        placeholderParser.visitData(tSupplier.get(), visitor);
    }
}
