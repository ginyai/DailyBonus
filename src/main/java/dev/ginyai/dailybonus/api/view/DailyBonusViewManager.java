package dev.ginyai.dailybonus.api.view;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.Optional;

public interface DailyBonusViewManager {

    Optional<DailyBonusView> createView(Player player, String id);

    Collection<String> getUsableViews();

}
