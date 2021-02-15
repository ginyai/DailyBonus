package dev.ginyai.dailybonus.bonus;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.data.TrackedPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class BonusRequirementPermission implements BonusRequirement {

    private final DailyBonusMain dailyBonus;
    private final String permission;
    private final Text display;

    public BonusRequirementPermission(DailyBonusMain dailyBonus, String permission, Text display) {
        this.dailyBonus = dailyBonus;
        this.permission = permission;
        this.display = display;
    }

    @Override
    public boolean check(PlayerData player) {
        if (player instanceof TrackedPlayer) {
            return ((TrackedPlayer) player).checkPermission(permission);
        } else {
            return false;
        }
    }

    @Override
    public Text format(Player player) {
        if (display != null) {
            return display;
        } else {
            return dailyBonus.getI18n().translateToLocal("requirement.permission.format", ImmutableMap.of("permission", permission));
        }
    }
}
