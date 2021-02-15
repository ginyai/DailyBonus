package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CycleBonusSet extends AbstractBonusSet {
    private final TimeCycle cycle;

    public CycleBonusSet(DailyBonusMain dailyBonus, String id, Text display, Text extraInfo, List<BonusRequirement> bonusRequirements, List<BonusEntry> bonusEntries, TimeCycle cycle) {
        super(dailyBonus, id, display, extraInfo, bonusRequirements, bonusEntries);
        this.cycle = cycle;
    }

    public TimeCycle getCycle() {
        return cycle;
    }

    @Override
    public CompletableFuture<Boolean> hasReceived(Player player) {
        DailyBonusTimeService timeService = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class);
        TimeRange<LocalDateTime> timeRange = timeService.getCurrentCycle(cycle);
        Instant start = timeService.toInstance(timeRange.getStart());
        Instant end = timeService.toInstance(timeRange.getEnd());
        return CompletableFuture.supplyAsync(() -> dailyBonus.getStorage()
            .checkReceived(getId(), player.getUniqueId(), start, end));
    }

    @Override
    public CompletableFuture<Boolean> markReceived(Player player) {
        DailyBonusTimeService timeService = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class);
        TimeRange<LocalDateTime> timeRange = timeService.getCurrentCycle(cycle);
        Instant start = timeService.toInstance(timeRange.getStart());
        Instant end = timeService.toInstance(timeRange.getEnd());
        Instant now = Instant.now();
        if (start.isBefore(now) && end.isAfter(now)) {
            return CompletableFuture.supplyAsync(() -> dailyBonus.getStorage()
                .checkAndMarkReceived(getId(), player.getUniqueId(), now, start, end));
        } else {
            return CompletableFuture.completedFuture(false);
        }
    }
}
