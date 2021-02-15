package dev.ginyai.dailybonus.bonus;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BonusRequirementSignCount implements BonusRequirement {
    private final DailyBonusMain dailyBonus;
    private final SignGroup group;
    private final int count;

    public BonusRequirementSignCount(DailyBonusMain dailyBonus, SignGroup group, int count) {
        this.dailyBonus = dailyBonus;
        this.group = group;
        this.count = count;
    }

    @Override
    public boolean check(PlayerData player) {
        return player.getPunchedDays(group) >= count;
    }

    @Override
    public Text format(Player player) {
        PlayerData playerData = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player);
        return dailyBonus.getI18n()
            .translateToLocal("requirement.sign_count.format", ImmutableMap.of(
                "sign-group", group.getDisplayName(),
                "count", count,
                "player", playerData,
                "player-count", playerData.getPunchedDays(group)));
    }
}
