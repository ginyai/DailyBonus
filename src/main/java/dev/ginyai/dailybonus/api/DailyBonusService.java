package dev.ginyai.dailybonus.api;

import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.data.IStorage;
import dev.ginyai.dailybonus.api.data.PlayerDataManager;
import dev.ginyai.dailybonus.api.view.DailyBonusViewManager;

import java.util.Collection;
import java.util.Optional;

public interface DailyBonusService {

    DailyBonusViewManager getViewManager();

    PlayerDataManager getPlayerDataManager();

    IStorage getStorage();

    Optional<SignGroup> getSignGroupById(String id);

    Collection<? extends SignGroup> getSignGroups();

    Optional<BonusSet> getBonusSetById(String id);

    Collection<? extends BonusSet> getBonusSets();
}
