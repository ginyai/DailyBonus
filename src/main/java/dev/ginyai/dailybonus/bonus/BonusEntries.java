package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.config.ConfigDeserializer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//todo: api
public final class BonusEntries {
    private Map<String, ConfigDeserializer<BonusEntry>> bonusEntriesMap = new ConcurrentHashMap<>();

    public void registry(String type, ConfigDeserializer<BonusEntry> deserializer) {
        bonusEntriesMap.put(type, deserializer);
    }

    public Optional<ConfigDeserializer<BonusEntry>> getDeserializer(String type) {
        return Optional.ofNullable(bonusEntriesMap.get(type));
    }
}
