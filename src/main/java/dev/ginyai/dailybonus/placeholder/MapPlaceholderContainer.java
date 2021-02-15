package dev.ginyai.dailybonus.placeholder;

import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public class MapPlaceholderContainer implements IPlaceholderContainer {
    private final Map<String, ?> map;

    public MapPlaceholderContainer(Map<String, ?> map) {
        this.map = map;
    }

    @Nullable
    @Override
    public Object parsePlaceholder(String... args) {
        if (args.length == 0) {
            return null;
        }
        StringBuilder builder = null;
        for (int i = 0; i< args.length; i++) {
            if (builder == null) {
                builder = new StringBuilder(args[i]);
            } else {
                builder.append("_");
                builder.append(args[i]);
            }
            String key = builder.toString();
            Object o = map.get(key);
            if (o != null) {
                if (o instanceof IPlaceholderContainer) {
                    return ((IPlaceholderContainer) o).parsePlaceholder(Arrays.copyOfRange(args, i + 1, args.length));
                } else {
                    return o;
                }
            }
        }
        return null;
    }

    @Override
    public void visitData(PlaceholderVisitor visitor) {
        map.forEach(visitor::visit);
    }
}
