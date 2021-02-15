package dev.ginyai.dailybonus.api.bonus;

import dev.ginyai.dailybonus.api.time.TimeRange;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface SignGroup {
    TimeRange<LocalDateTime> getActiveTime();

    String getId();

    Text getDisplayName();

    boolean isUsable(Player player);

    CompletableFuture<Boolean> isSigned(Player player, LocalDate date);

    CompletableFuture<Boolean> canSignToday(Player player);

    CompletableFuture<Boolean> sign(Player player);

    CompletableFuture<Boolean> sign(Player player, LocalDate date);

    CompletableFuture<Integer> count(Player player);
}
