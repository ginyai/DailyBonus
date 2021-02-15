package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.text.Text;

import java.time.LocalDateTime;

public class OnceSignGroup extends AbstractSignGroup {
    private final TimeRange<LocalDateTime> timeRange;

    public OnceSignGroup(DailyBonusMain dailyBonus, String id, String dataId, Text display, TimeRange<LocalDateTime> timeRange) {
        super(dailyBonus, id, dataId, display);
        this.timeRange = timeRange;
    }

    @Override
    public TimeRange<LocalDateTime> getActiveTime() {
        return timeRange;
    }
}
