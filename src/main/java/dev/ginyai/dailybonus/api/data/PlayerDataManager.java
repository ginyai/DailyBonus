package dev.ginyai.dailybonus.api.data;

import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CompletableFuture;

public interface PlayerDataManager {
    PlayerData getOrCreatePlayerData(Player player);

    CompletableFuture<? extends PlayerData> updatePlayerData(Player player);

    void onPlayerJoin(Player player);

    void onPlayerLeave(Player player);
}
