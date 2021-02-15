package dev.ginyai.dailybonus.compact;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.util.LoadingIssuesTracker;
import org.spongepowered.api.Sponge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Compacts {
    private static final Map<String, List<String>> compacts = ImmutableMap.of(
        "placeholderapi", ImmutableList.of("PapiCompact")
    );

    private static final List<ICompact> loadedCompacts = new ArrayList<>();

    public static void init(DailyBonusMain dailyBonus, LoadingIssuesTracker tracker) {
        for (Map.Entry<String, List<String>> entry: compacts.entrySet()) {
            String depend = entry.getKey();
            if (!depend.isEmpty() && !Sponge.getPluginManager().isLoaded(depend)) {
                continue;
            }
            for (String s: entry.getValue()) {
                try {
                    Class<? extends ICompact> c = (Class<? extends ICompact>) Class.forName("dev.ginyai.dailybonus.compact." + s);
                    ICompact compact = c.newInstance();
                    compact.init(dailyBonus, tracker);
                } catch (Exception e) {
                    tracker.error("Unable to init compact " + s, e);
                }
            }
        }
    }

    public static void reload(DailyBonusMain dailyBonus, LoadingIssuesTracker tracker) {
        for (ICompact compact : loadedCompacts) {
            try {
                compact.loadConfig(dailyBonus, tracker);
            } catch (Exception e) {
                tracker.error("Compact " + compact.getClass().getName() + " failed to load config.", e);
            }
        }
    }
}
