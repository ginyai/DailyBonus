package dev.ginyai.dailybonus.api.bonus;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface BonusSet {

    String getId();

    Text getDisplay();

    Optional<Text> getExtraInfo();

    CompletableFuture<Boolean> hasReceived(Player player);

    List<BonusRequirement> getRequirements();

    List<BonusEntry> getBonusEntries();

    CompletableFuture<GiveResult> give(Player player);

    interface GiveResult {
        boolean isSuccessful();

        Optional<Text> getFailMessage();
    }
}
