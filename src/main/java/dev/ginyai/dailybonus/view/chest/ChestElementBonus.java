package dev.ginyai.dailybonus.view.chest;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.bonus.SimpleBonusGiveResult;
import dev.ginyai.dailybonus.placeholder.BonusSetValueContainer;
import dev.ginyai.dailybonus.placeholder.DailyBonusPlaceholders;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ChestElementBonus extends ChestElement {

    private final DailyBonusMain dailyBonus;
    private final BonusSet bonusSet;

    private final Function<Function<String, String>, ItemStack> receivedFunction;
    private final Function<Function<String, String>, ItemStack> usableFunction;
    private final Function<Function<String, String>, ItemStack> unusableFunction;

    public ChestElementBonus(DailyBonusMain dailyBonus, BonusSet bonusSet, Function<Function<String, String>, ItemStack> receivedFunction, Function<Function<String, String>, ItemStack> usableFunction, Function<Function<String, String>, ItemStack> unusableFunction) {
        this.dailyBonus = dailyBonus;
        this.bonusSet = bonusSet;
        this.receivedFunction = receivedFunction;
        this.usableFunction = usableFunction;
        this.unusableFunction = unusableFunction;
    }

    public BonusSet getBonusSet() {
        return bonusSet;
    }

    @Override
    public ItemStack getDisplay(PlayerData owner) {
        DailyBonusPlaceholders placeholders = dailyBonus.getPlaceholders();
        Map<String, ?> map = ImmutableMap.of(
            "player", owner,
            "bonus", new BonusSetValueContainer(owner, bonusSet)
        );
        Function<String, String> replacePlaceholder = s -> placeholders.replacePlaceholders(s, map);
        if (owner.isReceived(bonusSet)) {
            return receivedFunction.apply(replacePlaceholder);
        } else if (bonusSet.getRequirements().stream().allMatch(r -> r.check(owner))) {
            return usableFunction.apply(replacePlaceholder);
        } else {
            return unusableFunction.apply(replacePlaceholder);
        }
    }

    @Override
    public Optional<CompletableFuture<?>> onClick(Player owner) {
        PlayerData data = dailyBonus.getPlayerDataManager().getOrCreatePlayerData(owner);
        for (BonusRequirement requirement: bonusSet.getRequirements()) {
            if (!requirement.check(data)) {
                return Optional.of(CompletableFuture.completedFuture(new SimpleBonusGiveResult(false, requirement.format(owner))));
            }
        }
        return Optional.of(bonusSet.give(owner));//todo: handle result
    }
}
