package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractBonusSet implements BonusSet {

    protected final DailyBonusMain dailyBonus;
    private final String id;
    private Text display;
    @Nullable
    private Text extraInfo;
    private List<BonusRequirement> requirements;
    private List<BonusEntry> entries;
    private boolean autoComplete;

    protected AbstractBonusSet(DailyBonusMain dailyBonus, String id, Text display, @Nullable Text extraInfo, List<BonusRequirement> requirements, List<BonusEntry> entries, boolean autoComplete) {
        this.dailyBonus = dailyBonus;
        this.id = id;
        this.display = display;
        this.extraInfo = extraInfo;
        this.requirements = requirements;
        this.entries = entries;
        this.autoComplete = autoComplete;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Text getDisplay() {
        return display;
    }

    @Override
    public Optional<Text> getExtraInfo() {
        return Optional.ofNullable(extraInfo);
    }

    @Override
    public abstract CompletableFuture<Boolean> hasReceived(Player player);

    public abstract CompletableFuture<Boolean> markReceived(Player player);

    @Override
    public List<BonusRequirement> getRequirements() {
        return requirements;
    }

    @Override
    public List<BonusEntry> getBonusEntries() {
        return entries;
    }

    @Override
    public CompletableFuture<GiveResult> give(Player player) {
        return markReceived(player)
            .thenApply(b -> {
                if (b) {
                    return give_(player);
                } else {
                    return new SimpleBonusGiveResult(false, dailyBonus.getI18n().translateToLocal("bonus_set.received"));
                }
            });
    }

    protected GiveResult give_(Player player) {
        entries.forEach(e -> e.give(player));
        //todo:
        return new SimpleBonusGiveResult(true, null);
    }

    public boolean isAutoComplete() {
        return autoComplete;
    }
}
