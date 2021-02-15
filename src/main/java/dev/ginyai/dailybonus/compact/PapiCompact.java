package dev.ginyai.dailybonus.compact;

import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.DailyBonusPlugin;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.util.LoadingIssuesTracker;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.Token;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PapiCompact implements ICompact {
    private DailyBonusMain dailyBonus;

    @Override
    public void init(DailyBonusMain dailyBonus, LoadingIssuesTracker tracker) {
        this.dailyBonus = dailyBonus;
        try {
            PlaceholderService placeholderService = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
            placeholderService.load(this, "dailybonus", DailyBonusPlugin.getInstance())
                .tokens("onlinetimetoday", "onlinetimetotal", "bonusset", "signgroup")
                .description("Placeholders of DailyBonus")
                .buildAndRegister();
        } catch (Exception e) {
            tracker.error("Failed to register placeholder dailybonus to Placeholder API", e);
        }
        try {
            PlaceholderService placeholderService = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
            placeholderService.load(this, "dailybonusdate", DailyBonusPlugin.getInstance())
                .description("Placeholders of DailyBonus' 'today'")
                .buildAndRegister();
        } catch (Exception e) {
            tracker.error("Failed to register placeholder dailybonusdate to Placeholder API", e);
        }
    }

    @Override
    public void loadConfig(DailyBonusMain dailyBonus, LoadingIssuesTracker tracker) {

    }

    @Placeholder(id = "dailybonus")
    public Object getValue(@Token(fix = true) String token, @Source Player player) {
        PlayerData playerData = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player);
        return playerData.parsePlaceholder(token.split(" "));
    }

    @Placeholder(id = "dailybonusdate")
    public Object getDate(@Nullable @Token(fix = false) String token) {
        LocalDate today = dailyBonus.getToday();
        if (token == null) {
            return today.toString();
        } else {
            return today.format(DateTimeFormatter.ofPattern(token));
        }
    }

}
