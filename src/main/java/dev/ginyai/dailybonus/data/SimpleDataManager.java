package dev.ginyai.dailybonus.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.data.PlayerDataManager;
import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SimpleDataManager implements PlayerDataManager {
    private final Map<UUID, TrackedPlayer> loadedData = new ConcurrentHashMap<>();
    private final DailyBonusMain dailyBonus;
    private final Function<UUID, ? extends TrackedPlayer> playerFactory;

    public SimpleDataManager(DailyBonusMain dailyBonus, Function<UUID, ? extends TrackedPlayer> playerFactory) {
        this.dailyBonus = dailyBonus;
        this.playerFactory = playerFactory.andThen(player -> {
            player.setDailyBonus(dailyBonus);
            return player;
        });
    }

    @Override
    public TrackedPlayer getOrCreatePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        return loadedData.computeIfAbsent(uuid, playerFactory.andThen(p -> fillPlayerData(player, p)));
    }

    @Override
    public CompletableFuture<TrackedPlayer> updatePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        TrackedPlayer trackedPlayer = loadedData.computeIfAbsent(uuid, playerFactory);
        return CompletableFuture.supplyAsync(() -> fillPlayerData(player, trackedPlayer));
    }

    public TrackedPlayer fillPlayerData(Player p, TrackedPlayer player) {
        UUID uuid = player.getUniqueId();
        String name = p.getName();
        boolean flag = player.getOnlineTimeTotal() == 0;
        Map<SignGroup, Integer> punchedDays = dailyBonus.getSignGroups().stream()
            .collect(ImmutableMap.toImmutableMap(Function.identity(), sign -> sign.count(p).join()));
        Set<BonusSet> receivedBonus = dailyBonus.getBonusSets().stream()
            .filter(s -> s.hasReceived(p).join()).collect(ImmutableSet.toImmutableSet());
        if (flag) {
            LocalDate today = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class).getToday();
            long onlineToday = dailyBonus.getStorage().getOnlineTime(uuid, today);
            long onlineTotal = dailyBonus.getStorage().getTotalOnlineTime(uuid);
            player.setData(name, onlineToday, today, onlineTotal, punchedDays, receivedBonus);
        } else {
            player.setData(name, punchedDays, receivedBonus);
        }
        return player;
    }

    public void savePlayerData(TrackedPlayer player) {
        LocalDate date = player.getToday();
        dailyBonus.getStorage().setOnlineTime(player.getUniqueId(), date, player.getOnlineTimeToday());
    }

    public void tick() {
        loadedData.values().forEach(TrackedPlayer::tick);
    }

    public CompletableFuture<TrackedPlayer> onPlayerJoin(Player player) {
        return updatePlayerData(player).whenComplete(((trackedPlayer, throwable) -> {
            if (throwable != null) {
                dailyBonus.getLogger().error("Exception on updating player data.", throwable);
            }
        }));
    }

    public CompletableFuture<Void> onPlayerLeave(Player player) {
        TrackedPlayer trackedPlayer = loadedData.remove(player.getUniqueId());
        if (trackedPlayer != null) {
            return CompletableFuture.runAsync(() -> savePlayerData(trackedPlayer))
                .whenComplete((aVoid, throwable) -> {
                    if (throwable != null) {
                        dailyBonus.getLogger().error("Failed to save player online time data.", throwable);
                    }
                });
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    public void saveAll() {
        loadedData.values().forEach(this::savePlayerData);
    }

    public void onReload() {
        loadedData.clear();
        Sponge.getServer().getOnlinePlayers().forEach(this::onPlayerJoin);
    }
}
