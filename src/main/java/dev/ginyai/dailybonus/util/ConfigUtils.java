package dev.ginyai.dailybonus.util;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.config.ConfigDeserializer;
import dev.ginyai.dailybonus.time.DateParser;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class ConfigUtils {
    public static <T> T readNonnull(ConfigurationNode node, ConfigDeserializer<T> function) throws ObjectMappingException {
        T t = function.deserialize(node);
        if (node.isVirtual() || t == null) {
            throw new ObjectMappingException(formatNodePath(node) + " is unset.");
        }
        return t;
    }

    public static String formatNodePath(ConfigurationNode node) {
        return Arrays.stream(node.getPath()).map(String::valueOf).collect(Collectors.joining("."));
    }

    public static long readTimespan(ConfigurationNode node) throws ObjectMappingException {
        String timespan = readNonnull(node, ConfigurationNode::getString);
        return DateParser.parseTime(timespan, () -> new ObjectMappingException("Invalid timespan argument. Argument:[" + timespan + "] @" + formatNodePath(node)));
    }

    public static <T> Map<String, T> readMap(ConfigurationNode node, ConfigDeserializer<T> deserializer) throws ObjectMappingException {
        ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry: node.getChildrenMap().entrySet()) {
            builder.put(entry.getKey().toString(), deserializer.deserialize(entry.getValue()));
        }
        return builder.build();
    }
}
