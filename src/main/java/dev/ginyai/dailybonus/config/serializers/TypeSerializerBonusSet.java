package dev.ginyai.dailybonus.config.serializers;

import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.bonus.CycleBonusSet;
import dev.ginyai.dailybonus.bonus.OnceBonusSet;
import dev.ginyai.dailybonus.config.ConfigLoadingTracker;
import dev.ginyai.dailybonus.util.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class TypeSerializerBonusSet implements TypeSerializer<BonusSet> {
    private final DailyBonusMain dailyBonus;

    public TypeSerializerBonusSet(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    @Nullable
    @Override
    public BonusSet deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
        String id = ConfigLoadingTracker.INSTANCE.getCurPrefix() + "." + ConfigUtils.readNonnull(node.getNode("Id"), ConfigurationNode::getString);
        Text display = dailyBonus.getTextParser().apply(ConfigUtils.readNonnull(node.getNode("Display"), ConfigurationNode::getString));
        Text extra = Optional.ofNullable(node.getNode("ExtraInfo").getString()).map(dailyBonus.getTextParser()).orElse(null);
        List<BonusRequirement> requirements = node.getNode("Requirements").getList(TypeToken.of(BonusRequirement.class));
        List<BonusEntry> entries = node.getNode("Entries").getList(TypeToken.of(BonusEntry.class));
        TimeCycle cycle = node.getNode("Cycle").getValue(TypeToken.of(TimeCycle.class), TimeCycle.ONCE);
        boolean autoComplete = node.getNode("AutoComplete").getBoolean(false);
        if (cycle == TimeCycle.ONCE) {
            return new OnceBonusSet(dailyBonus, id, display, extra, requirements, entries, autoComplete);
        } else {
            return new CycleBonusSet(dailyBonus, id, display, extra, requirements, entries, cycle, autoComplete);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable BonusSet obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        throw new UnsupportedOperationException("serialize BonusSet");
    }
}
