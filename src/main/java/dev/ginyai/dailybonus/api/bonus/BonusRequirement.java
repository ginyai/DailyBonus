package dev.ginyai.dailybonus.api.bonus;

import dev.ginyai.dailybonus.api.data.PlayerData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public interface BonusRequirement {
    boolean check(PlayerData player);

    Text format(Player player);
}
