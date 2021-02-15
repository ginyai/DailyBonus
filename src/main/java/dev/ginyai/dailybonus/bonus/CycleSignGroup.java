package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.time.LocalDateTime;

public class CycleSignGroup extends AbstractSignGroup {
    private final TimeCycle cycle;

    public CycleSignGroup(DailyBonusMain dailyBonus, String id, Text display, TimeCycle cycle) {
        super(dailyBonus, id, display);
        this.cycle = cycle;
    }

    public TimeCycle getCycle() {
        return cycle;
    }

    @Override
    public TimeRange<LocalDateTime> getActiveTime() {
        DailyBonusTimeService timeService = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class);
        return timeService.getCurrentCycle(cycle);
    }

    @Override
    public boolean isUsable(Player player) {
        return true;
    }
}
