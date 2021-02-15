package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.config.ConfigDeserializer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//todo: api
public final class BonusRequirements {
    private final Map<String, ConfigDeserializer<BonusRequirement>> map = new ConcurrentHashMap<>();

    public void registry(String type, ConfigDeserializer<BonusRequirement> deserializer) {
        map.put(type, deserializer);
    }

    public Optional<ConfigDeserializer<BonusRequirement>> getDeserializer(String type) {
        return Optional.ofNullable(map.get(type));
    }
}
