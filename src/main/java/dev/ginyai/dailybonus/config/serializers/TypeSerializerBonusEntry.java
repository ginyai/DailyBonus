package dev.ginyai.dailybonus.config.serializers;

import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.bonus.BonusEntries;
import dev.ginyai.dailybonus.config.ConfigDeserializer;
import dev.ginyai.dailybonus.util.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TypeSerializerBonusEntry implements TypeSerializer<BonusEntry> {
    private final BonusEntries bonusEntries;

    public TypeSerializerBonusEntry(BonusEntries bonusEntries) {
        this.bonusEntries = bonusEntries;
    }

    @Nullable
    @Override
    public BonusEntry deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
        String typeString = ConfigUtils.readNonnull(node.getNode("Type"), ConfigurationNode::getString);
        ConfigDeserializer<BonusEntry> deserializer = bonusEntries.getDeserializer(typeString).orElseThrow(() -> new ObjectMappingException("Unsupported bonus entry type: " + typeString));
        return deserializer.deserialize(node);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable BonusEntry obj, @NonNull ConfigurationNode node) throws ObjectMappingException {
        throw new UnsupportedOperationException("serialize bonus entry.");
    }
}
