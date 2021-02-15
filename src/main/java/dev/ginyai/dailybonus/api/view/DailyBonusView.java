package dev.ginyai.dailybonus.api.view;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public interface DailyBonusView {

    void open();

    Optional<Player> getOwner();
}
