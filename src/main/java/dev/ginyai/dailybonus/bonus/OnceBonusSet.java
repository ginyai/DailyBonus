package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OnceBonusSet extends AbstractBonusSet {
    public OnceBonusSet(DailyBonusMain dailyBonus, String id, Text display, Text extraInfo, List<BonusRequirement> bonusRequirements, List<BonusEntry> bonusEntries, boolean autoComplete) {
        super(dailyBonus, id, display, extraInfo, bonusRequirements, bonusEntries, autoComplete);
    }

    @Override
    public CompletableFuture<Boolean> hasReceived(Player player) {
        return CompletableFuture.supplyAsync(() -> dailyBonus.getStorage().checkReceived(getId(), player.getUniqueId()));
    }

    @Override
    public CompletableFuture<Boolean> markReceived(Player player) {
        return CompletableFuture.supplyAsync(() -> dailyBonus.getStorage().checkAndMarkReceived(getId(), player.getUniqueId(), Instant.now()));
    }
}
