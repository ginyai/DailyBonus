package dev.ginyai.dailybonus.placeholder;

import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class PlaceholderVisitor implements IPlaceholderContainer.PlaceholderVisitor {
    private final Map<String, Object> values = new HashMap<>();
    private final LinkedList<String> prefix = new LinkedList<>();

    @Override
    public void visit(String key, Object value) {
        String prefixString = prefix.isEmpty() ? key : String.join("_", prefix) + "_" + key;
        if (value instanceof IPlaceholderContainer) {
            Object o = ((IPlaceholderContainer) value).parsePlaceholder();
            if (o != null) {
                values.put(prefixString, o);
            }
            prefix.push(key);
            ((IPlaceholderContainer) value).visitData(this);
            prefix.pop();
        } else {
            values.put(prefixString, value);
        }
    }

    public Map<String, ?> getValues() {
        return values;
    }
}
