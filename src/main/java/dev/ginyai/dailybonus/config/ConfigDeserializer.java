package dev.ginyai.dailybonus.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@FunctionalInterface
public interface ConfigDeserializer<T> {
    T deserialize(ConfigurationNode node) throws ObjectMappingException;
}
