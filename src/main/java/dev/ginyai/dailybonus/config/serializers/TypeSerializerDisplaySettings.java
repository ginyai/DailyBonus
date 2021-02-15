package dev.ginyai.dailybonus.config.serializers;

import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.config.ChestViewDisplaySettings;
import dev.ginyai.dailybonus.view.chest.ChestElement;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TypeSerializerDisplaySettings implements TypeSerializer<ChestViewDisplaySettings> {
    private final DailyBonusMain dailyBonus;

    public TypeSerializerDisplaySettings(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    @Nullable
    @Override
    public ChestViewDisplaySettings deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        int size = value.getNode("Size").getInt(54);
        String title = value.getNode("Title").getString("");
        Map<Integer, ChestElement> elements = new HashMap<>();
        for (Map.Entry<?, ? extends ConfigurationNode> entry: value.getNode("Elements").getChildrenMap().entrySet()) {
            int slot = Integer.parseInt(String.valueOf(entry.getKey()));
            ChestElement element = entry.getValue().getValue(TypeToken.of(ChestElement.class));
            if (element == null) {
                throw new ObjectMappingException(slot + " is unset.");
            }
            elements.put(slot, element);
        }
        return new ChestViewDisplaySettings(size, title, elements);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ChestViewDisplaySettings obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException();
    }
}
