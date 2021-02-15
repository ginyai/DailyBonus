package dev.ginyai.dailybonus.config.serializers;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.config.ConfigLoadingTracker;
import dev.ginyai.dailybonus.util.ConfigUtils;
import dev.ginyai.dailybonus.view.chest.ChestElement;
import dev.ginyai.dailybonus.view.chest.ChestElementBonus;
import dev.ginyai.dailybonus.view.chest.ChestElementFixed;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeSerializerChestElement implements TypeSerializer<ChestElement> {
    private final DailyBonusMain dailyBonus;

    public TypeSerializerChestElement(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    @Override
    public @Nullable ChestElement deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
        String typeString = ConfigUtils.readNonnull(node.getNode("Type"), ConfigurationNode::getString);
        switch (typeString.toLowerCase(Locale.ROOT)) {
            case "fixed":
                return new ChestElementFixed(dailyBonus, readItem(node.getNode("Item")));
            case "bonus":
                String bonusSetString = ConfigUtils.readNonnull(node.getNode("BonusSet"), ConfigurationNode::getString);
                BonusSet bonusSet = dailyBonus.getBonusSetById(ConfigLoadingTracker.INSTANCE.addPrefix(bonusSetString)).orElseThrow(() -> new ObjectMappingException("Unable to find bonus with id " + bonusSetString));
                return new ChestElementBonus(dailyBonus, bonusSet, readItem(node.getNode("ItemReceived")), readItem(node.getNode("ItemUsable")), readItem(node.getNode("ItemUnusable")));
            default:
                throw new ObjectMappingException("Unsupported ChestElement Type: " + typeString);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ChestElement obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        // TODO: 2021/2/11 IMPL serialize
        throw new UnsupportedOperationException("TODO");
    }

    private static Function<Function<String, String>, ItemStack> readItem(ConfigurationNode node) throws ObjectMappingException {
        if (node.isVirtual()) {
            return f -> ItemStack.empty();
        }
        DataContainer container = DataTranslators.CONFIGURATION_NODE.translate(node);
        if (!container.contains(DataQuery.of("ItemType"))) {
            throw new ObjectMappingException("`ItemType` unset @" + ConfigUtils.formatNodePath(node));
        }
        if (!container.contains(DataQuery.of("Count"))) {
            throw new ObjectMappingException("`Count` unset @" + ConfigUtils.formatNodePath(node));
        }
        if (!container.contains(DataQuery.of("UnsafeDamage"))) {
            throw new ObjectMappingException("`UnsafeDamage` unset @" + ConfigUtils.formatNodePath(node));
        }
        return f -> process(f, container);
    }

    private static final Pattern PATTERN_WITH_FORMAT = Pattern.compile("\\{(?<type>[silfd]):(?<value>.+)}");
    private static final Map<String, Function<String, Object>> FORMAT_PARSER = ImmutableMap.<String, Function<String, Object>>builder()
        .put("s", Short::parseShort)
        .put("i", Integer::parseInt)
        .put("l", Long::parseLong)
        .put("f", Float::parseFloat)
        .put("d", Double::parseDouble)
        .build();

    private static ItemStack process(Function<String, String> function, DataContainer dataContainer) {
        for (Map.Entry<DataQuery, Object> entry: dataContainer.getValues(true).entrySet()) {
            if (entry.getValue() instanceof String) {
                String s = (String) entry.getValue();
                Object fValue;
                Matcher matcher = PATTERN_WITH_FORMAT.matcher(s);
                if (matcher.matches()) {
                    fValue = FORMAT_PARSER.get(matcher.group("type")).apply(function.apply(matcher.group("value")));
                } else {
                    fValue = function.apply((String) entry.getValue());
                }
                dataContainer.set(entry.getKey(), fValue);
            } else if (entry.getValue() instanceof List) {
                //noinspection unchecked
                List<Object> list = (List<Object>) entry.getValue();
                List<Object> nList = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof String) {
                        String s = function.apply((String) o);
                        if (s.contains("\n")) {
                            nList.addAll(Arrays.asList(s.split("\\n")));
                        } else {
                            nList.add(s);
                        }
                    } else {
                        nList.add(o);
                    }
                }
                dataContainer.set(entry.getKey(), nList);
            }
        }
        return ItemStack.builder().fromContainer(dataContainer).build();
    }
}
