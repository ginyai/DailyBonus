package dev.ginyai.dailybonus.api.bonus;

import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CompletableFuture;

public interface BonusEntry {
    CompletableFuture<Boolean> canGive(Player player);

    CompletableFuture<BonusSet.GiveResult> give(Player player);

}
