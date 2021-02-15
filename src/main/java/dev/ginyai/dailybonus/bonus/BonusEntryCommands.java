package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BonusEntryCommands implements BonusEntry {

    private DailyBonusMain dailyBonus;
    private List<String> commands;

    public BonusEntryCommands(DailyBonusMain dailyBonus, List<String> commands) {
        this.dailyBonus = dailyBonus;
        this.commands = commands;
    }

    @Override
    public CompletableFuture<Boolean> canGive(Player player) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<BonusSet.GiveResult> give(Player player) {
        if (Sponge.getServer().isMainThread()) {
            dispatchCommands(player);
            return CompletableFuture.completedFuture(new SimpleBonusGiveResult(true, null));
        } else {
            return CompletableFuture.supplyAsync(() -> {
                dispatchCommands(player);
                return new SimpleBonusGiveResult(true, null);
            }, dailyBonus.getSyncExecutor());
        }
    }

    private void dispatchCommands(Player player) {
        commands.stream().map(s -> dailyBonus.getPlaceholders().replaceCommandPlaceholders(s, player))
            .forEach(dailyBonus::dispatchConsoleCommand);
    }
}
