package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BonusEntrySign implements BonusEntry {

    private final SignGroup signGroup;

    public BonusEntrySign(SignGroup signGroup) {
        this.signGroup = Objects.requireNonNull(signGroup, "signGroup");
    }

    @Override
    public CompletableFuture<Boolean> canGive(Player player) {
        return signGroup.canSignToday(player);
    }

    @Override
    public CompletableFuture<BonusSet.GiveResult> give(Player player) {
        return signGroup.sign(player)
            //todo: the result is always true now
            .thenApply(aBoolean -> new SimpleBonusGiveResult(aBoolean, aBoolean ? null : /*todo: get message*/null));
    }
}
