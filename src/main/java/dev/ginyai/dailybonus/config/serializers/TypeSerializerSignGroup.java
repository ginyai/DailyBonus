package dev.ginyai.dailybonus.config.serializers;

import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.bonus.CycleSignGroup;
import dev.ginyai.dailybonus.bonus.OnceSignGroup;
import dev.ginyai.dailybonus.config.ConfigLoadingTracker;
import dev.ginyai.dailybonus.util.ConfigUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TypeTokens;

import java.time.LocalDateTime;

public class TypeSerializerSignGroup implements TypeSerializer<SignGroup> {
    private final DailyBonusMain dailyBonus;

    public TypeSerializerSignGroup(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    @Nullable
    @Override
    public SignGroup deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode node) throws ObjectMappingException {
        String id = ConfigLoadingTracker.INSTANCE.getCurPrefix() + "." + ConfigUtils.readNonnull(node.getNode("Id"), ConfigurationNode::getString);
        String dataId = node.getNode("DataId").getString(id);
        //todo: use text parser
        Text display = ConfigUtils.readNonnull(node.getNode("Display"), n -> n.getValue(TypeTokens.TEXT_TOKEN));
        TimeCycle cycle = ConfigUtils.readNonnull(node.getNode("Cycle"), n -> n.getValue(TypeToken.of(TimeCycle.class)));
        if (cycle == TimeCycle.ONCE) {
            LocalDateTime start = LocalDateTime.parse(ConfigUtils.readNonnull(node.getNode("Start"), ConfigurationNode::getString));
            LocalDateTime end = LocalDateTime.parse(ConfigUtils.readNonnull(node.getNode("End"), ConfigurationNode::getString));
            return new OnceSignGroup(dailyBonus, id, dataId, display, new TimeRange<>(start, end));
        } else {
            return new CycleSignGroup(dailyBonus, id, dataId, display, cycle);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SignGroup obj, @NonNull ConfigurationNode node) throws ObjectMappingException {
        if (obj == null) {
            return;
        }
        node.getNode("Id").setValue(obj.getId());
        node.getNode("Display").setValue(obj.getDisplayName());
        if (obj instanceof CycleSignGroup) {
            node.getNode("Cycle").setValue(((CycleSignGroup) obj).getCycle());
        } else {
            node.getNode("Cycle").setValue(TimeCycle.ONCE);
            TimeRange<LocalDateTime> timeRange = obj.getActiveTime();
            node.getNode("Start").setValue(timeRange.getStart().toString());
            node.getNode("End").setValue(timeRange.getEnd().toString());
        }
    }
}
