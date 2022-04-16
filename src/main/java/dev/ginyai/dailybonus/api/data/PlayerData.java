package dev.ginyai.dailybonus.api.data;

import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Local cached player data for placeholder api & tops display
 */
public abstract class PlayerData implements IPlaceholderContainer {
    protected final UUID uuid;
    protected String name;
    protected long onlineTimeToday;
    protected long onlineTimeTotal;
    protected Map<SignGroup, Integer> punchedDays;
    protected Set<BonusSet> receivedBonus;

    protected PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    protected PlayerData(UUID uuid, String name, int onlineTimeToday, long onlineTimeTotal, Map<SignGroup, Integer> punchedDays, Set<BonusSet> receivedBonus) {
        this.uuid = uuid;
        this.name = name;
        this.onlineTimeToday = onlineTimeToday;
        this.onlineTimeTotal = onlineTimeTotal;
        this.punchedDays = punchedDays;
        this.receivedBonus = receivedBonus;
    }

    protected void setData(String name, long onlineTimeToday, long onlineTimeTotal, Map<SignGroup, Integer> punchedDays, Set<BonusSet> receivedBonus) {
        this.name = name;
        this.onlineTimeToday = onlineTimeToday;
        this.onlineTimeTotal = onlineTimeTotal;
        this.punchedDays = punchedDays;
        this.receivedBonus = receivedBonus;
    }

    public abstract Optional<Player> getPlayer();

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getOnlineTimeToday() {
        return onlineTimeToday;
    }

    public long getOnlineTimeTotal() {
        return onlineTimeTotal;
    }

    public int getPunchedDays(SignGroup group) {
        return punchedDays.getOrDefault(group, 0);
    }

    public boolean isReceived(BonusSet bonusSet) {
        return receivedBonus.contains(bonusSet);
    }

    public abstract void postUpdate();
}
