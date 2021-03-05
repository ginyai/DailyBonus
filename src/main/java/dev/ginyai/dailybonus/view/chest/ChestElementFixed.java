package dev.ginyai.dailybonus.view.chest;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ChestElementFixed extends ChestElement {

    private final DailyBonusMain dailyBonus;
    private final Function<Function<String, String>, ItemStack> itemStackFunction;

    public ChestElementFixed(DailyBonusMain dailyBonus, Function<Function<String, String>, ItemStack> itemStackFunction) {
        this.dailyBonus = dailyBonus;
        this.itemStackFunction = itemStackFunction;
    }

    @Override
    public ItemStack getDisplay(PlayerData data) {
        return itemStackFunction.apply(s -> dailyBonus.getPlaceholders().replacePlaceholders(s, ImmutableMap.of("player", data)));
    }

    @Override
    public Optional<CompletableFuture<?>> onClick(Player owner) {
        //do nothing
        return Optional.empty();
    }
}
