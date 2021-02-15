package dev.ginyai.dailybonus.view.chest;

import dev.ginyai.dailybonus.api.view.DailyBonusView;
import dev.ginyai.dailybonus.api.view.DailyBonusViewManager;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.config.ChestViewDisplaySettings;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ChestViewManager implements DailyBonusViewManager {

    private final DailyBonusMain dailyBonus;
    private final Supplier<Map<String, ChestViewDisplaySettings>> chestViewDisplaySettingsMap;

    public ChestViewManager(DailyBonusMain dailyBonus, Supplier<Map<String, ChestViewDisplaySettings>> chestViewDisplaySettingsMap) {
        this.dailyBonus = dailyBonus;
        this.chestViewDisplaySettingsMap = chestViewDisplaySettingsMap;
    }

    @Override
    public Optional<DailyBonusView> createView(Player player, String id) {
        ChestViewDisplaySettings displaySettings = chestViewDisplaySettingsMap.get().get(id);
        if (displaySettings == null) {
            return Optional.empty();
        } else {
            return Optional.of(new ChestViewHolder(dailyBonus, player, displaySettings));
        }
    }

    @Override
    public Collection<String> getUsableViews() {
        return chestViewDisplaySettingsMap.get().keySet();
    }
}
