package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractSignGroup implements SignGroup {
    protected final DailyBonusMain dailyBonus;
    protected final String id;
    protected final String dataId;
    protected Text display;

    protected AbstractSignGroup(DailyBonusMain dailyBonus, String id, String dataId, Text display) {
        this.dailyBonus = dailyBonus;
        this.id = id;
        this.dataId = dataId;
        this.display = display;
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public Text getDisplayName() {
        return display;
    }

    @Override
    public boolean isUsable(Player player) {
        return getActiveTime().isIn(LocalDateTime.now());
    }

    @Override
    public CompletableFuture<Boolean> isSigned(Player player, LocalDate date) {
        // TODO: 2021/2/15  IMPL
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public CompletableFuture<Boolean> canSignToday(Player player) {
        if (Sponge.getServer().isMainThread()) {
            return CompletableFuture.supplyAsync(() -> canSignToday_(player));
        } else {
            return CompletableFuture.completedFuture(canSignToday_(player));
        }
    }

    public boolean canSignToday_(Player player) {
        DailyBonusTimeService timeService = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class);
        UUID uuid = player.getUniqueId();
        return !dailyBonus.getStorage().checkPunched(dataId, uuid, timeService.getToday());
    }

    @Override
    public CompletableFuture<Boolean> sign(Player player) {
        DailyBonusTimeService timeService = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class);
        return sign(player, timeService.getToday());
    }

    @Override
    public CompletableFuture<Boolean> sign(Player player, LocalDate date) {
        if (Sponge.getServer().isMainThread()) {
            return CompletableFuture.supplyAsync(() -> sign_(player, date));
        } else {
            return CompletableFuture.completedFuture(sign_(player, date));
        }
    }

    public boolean sign_(Player player, LocalDate date) {
        UUID uuid = player.getUniqueId();
        dailyBonus.getStorage().punch(dataId, uuid, date, Instant.now());
        return true;
    }

    @Override
    public CompletableFuture<Integer> count(Player player) {
        if (Sponge.getServer().isMainThread()) {
            return CompletableFuture.supplyAsync(() -> count_(player));
        } else {
            return CompletableFuture.completedFuture(count_(player));
        }
    }

    public int count_(Player player) {
        TimeRange<LocalDateTime> timeRange = getActiveTime();
        return dailyBonus.getStorage()
            .countPunchPoints(getId(), player.getUniqueId(), timeRange.getStart().toLocalDate(), timeRange.getEnd().toLocalDate());
    }
}
