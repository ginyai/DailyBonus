package dev.ginyai.dailybonus.bonus;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.placeholder.TimeValueParser;
import dev.ginyai.dailybonus.placeholder.WarpedParer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BonusRequirementOnlineTimeToday implements BonusRequirement {

    private final DailyBonusMain dailyBonus;
    private final long requirement;

    public BonusRequirementOnlineTimeToday(DailyBonusMain dailyBonus, long requirement) {
        this.dailyBonus = dailyBonus;
        this.requirement = requirement;
    }

    @Override
    public boolean check(PlayerData player) {
        return player.getOnlineTimeToday() > requirement;
    }

    @Override
    public Text format(Player player) {
        PlayerData playerData = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player);
        return dailyBonus.getI18n()
            .translateToLocal("requirement.online_time_today.format", ImmutableMap.of(
                "target", new WarpedParer<>(new TimeValueParser(dailyBonus), () -> requirement),
                "player", playerData));
    }
}
