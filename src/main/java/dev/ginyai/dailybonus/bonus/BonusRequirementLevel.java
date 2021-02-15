package dev.ginyai.dailybonus.bonus;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.data.PlayerData;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BonusRequirementLevel implements BonusRequirement {
    private final DailyBonusMain dailyBonus;
    private final int level;

    public BonusRequirementLevel(DailyBonusMain dailyBonus, int level) {
        this.dailyBonus = dailyBonus;
        this.level = level;
    }

    @Override
    public boolean check(PlayerData player) {
        return player.getPlayer().flatMap(p -> p.get(Keys.EXPERIENCE_LEVEL)).map(leve -> leve>=this.level).orElse(false);
    }

    @Override
    public Text format(Player player) {
        return dailyBonus.getI18n()
            .translateToLocal("requirement.level.format", ImmutableMap.of("target", level, "current", player.get(Keys.EXPERIENCE_LEVEL).orElse(0)));
    }
}
