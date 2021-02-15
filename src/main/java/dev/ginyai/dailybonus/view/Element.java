package dev.ginyai.dailybonus.view;

import dev.ginyai.dailybonus.api.data.PlayerData;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class Element<E> {
    public abstract E getDisplay(PlayerData owner);

    public abstract Optional<CompletableFuture<?>> onClick(Player owner);
}
