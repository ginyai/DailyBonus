package dev.ginyai.dailybonus.config.serializers;

import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.bonus.CycleSignGroup;
import dev.ginyai.dailybonus.bonus.OnceSignGroup;
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
        String id = ConfigUtils.readNonnull(node.getNode("id"), ConfigurationNode::getString);
        //todo: use text parser
        Text display = ConfigUtils.readNonnull(node.getNode("display"), n -> n.getValue(TypeTokens.TEXT_TOKEN));
        TimeCycle cycle = ConfigUtils.readNonnull(node.getNode("cycle"), n -> n.getValue(TypeToken.of(TimeCycle.class)));
        if (cycle == TimeCycle.ONCE) {
            LocalDateTime start = LocalDateTime.parse(ConfigUtils.readNonnull(node.getNode("start"), ConfigurationNode::getString));
            LocalDateTime end = LocalDateTime.parse(ConfigUtils.readNonnull(node.getNode("end"), ConfigurationNode::getString));
            return new OnceSignGroup(dailyBonus, id, display, new TimeRange<>(start, end));
        } else {
            return new CycleSignGroup(dailyBonus, id, display, cycle);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable SignGroup obj, @NonNull ConfigurationNode node) throws ObjectMappingException {
        if (obj == null) {
            return;
        }
        node.getNode("id").setValue(obj.getId());
        node.getNode("display").setValue(obj.getDisplayName());
        if (obj instanceof CycleSignGroup) {
            node.getNode("cycle").setValue(((CycleSignGroup) obj).getCycle());
        } else {
            node.getNode("cycle").setValue(TimeCycle.ONCE);
            TimeRange<LocalDateTime> timeRange = obj.getActiveTime();
            node.getNode("start").setValue(timeRange.getStart().toString());
            node.getNode("end").setValue(timeRange.getEnd().toString());
        }
    }
}
