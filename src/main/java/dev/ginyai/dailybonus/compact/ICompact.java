package dev.ginyai.dailybonus.compact;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.util.LoadingIssuesTracker;

public interface ICompact {
    void init(DailyBonusMain dailyBonus, LoadingIssuesTracker tracker);

    void loadConfig(DailyBonusMain dailyBonus, LoadingIssuesTracker tracker);
}
