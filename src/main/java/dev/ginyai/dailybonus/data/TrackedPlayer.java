package dev.ginyai.dailybonus.data;

import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.placeholder.BonusSetValueContainer;
import dev.ginyai.dailybonus.placeholder.IPlaceholderParser;
import dev.ginyai.dailybonus.placeholder.SignGroupValueContainer;
import dev.ginyai.dailybonus.placeholder.TimeValueParser;
import dev.ginyai.dailybonus.placeholder.WarpedParer;
import dev.ginyai.dailybonus.util.UtilMethods;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TrackedPlayer extends PlayerData {
    private long lastTickTime;
    private LocalDate today;
    private DailyBonusMain dailyBonus;

    public TrackedPlayer(UUID uuid) {
        super(uuid);
    }

    @Override
    public Optional<Player> getPlayer() {
        return Sponge.getServer().getPlayer(uuid);
    }

    public void setDailyBonus(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public void setData(String name, long onlineTimeToday, LocalDate today, long onlineTimeTotal, Map<SignGroup, Integer> punchedDays, Set<BonusSet> receivedBonus) {
        super.setData(name, onlineTimeToday, onlineTimeTotal, punchedDays, receivedBonus);
        this.today = today;
        lastTickTime = System.currentTimeMillis();
    }

    public void setData(String name, Map<SignGroup, Integer> punchedDays, Set<BonusSet> receivedBonus) {
        this.name = name;
        this.punchedDays = punchedDays;
        this.receivedBonus = receivedBonus;
    }

    private IPlaceholderParser<Long> timeParser;

    private WarpedParer<Long> todayParser;
    private WarpedParer<Long> totalParser;
    private Map<SignGroup, SignGroupValueContainer> signGroupValueMap = new ConcurrentHashMap<>();
    private Map<BonusSet, BonusSetValueContainer> bonusSetValueMap = new ConcurrentHashMap<>();

    private void initParser() {
        if (timeParser == null) {
            timeParser = new TimeValueParser(dailyBonus);
            todayParser = new WarpedParer<>(timeParser, this::getOnlineTimeToday);
            totalParser = new WarpedParer<>(timeParser, this::getOnlineTimeTotal);
            dailyBonus.getSignGroups().forEach(signGroup ->
                signGroupValueMap.computeIfAbsent(signGroup, s -> new SignGroupValueContainer(this, s))
            );
            dailyBonus.getBonusSets().forEach(bonusSet ->
                bonusSetValueMap.computeIfAbsent(bonusSet, s -> new BonusSetValueContainer(this, s))
            );
        }
    }

    @Nullable
    @Override
    public Object parsePlaceholder(String... args) {
        initParser();
        if (args.length == 0) {
            return getName();
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "name":
                return getName();
            case "uuid":
                return getUniqueId();
            case "onlinetimetoday":
                return todayParser.parsePlaceholder(Arrays.copyOfRange(args, 1, args.length));
            case "onlinetimetotal":
                return totalParser.parsePlaceholder(Arrays.copyOfRange(args, 1, args.length));
            case "bonusset":
                if (args.length >= 2) {
                    BonusSet bonusSet = dailyBonus.getBonusSetById(args[1]).orElse(null);
                    if (bonusSet != null) {
                        return bonusSetValueMap.computeIfAbsent(bonusSet, s -> new BonusSetValueContainer(this, s))
                            .parsePlaceholder(Arrays.copyOfRange(args, 2, args.length));
                    }
                }
                break;
            case "signgroup":
                if (args.length >= 2) {
                    SignGroup signGroup = dailyBonus.getSignGroupById(args[1]).orElse(null);
                    if (signGroup != null) {
                        return signGroupValueMap.computeIfAbsent(signGroup, s -> new SignGroupValueContainer(this, s))
                            .parsePlaceholder(Arrays.copyOfRange(args, 2, args.length));
                    }
                }
                break;

        }
        //todo:
        return null;
    }

    @Override
    public void visitData(PlaceholderVisitor visitor) {
        initParser();
        visitor.visit("name", getName());
        visitor.visit("uuid", getUniqueId());
        visitor.visit("onlinetimetoday", todayParser);
        visitor.visit("onlinetimetotal", totalParser);
        bonusSetValueMap.forEach((s, v) -> {
            visitor.visit("bonusset_" + s.getId(), v);
        });
        signGroupValueMap.forEach((s, v) -> {
            visitor.visit("signgroup_" + s.getId(), v);
        });
    }

    public LocalDate getToday() {
        return today;
    }

    protected boolean shouldAddTime(Player owner) {
        //todo: afk support
        return true;
    }

    public boolean checkPermission(String permission) {
        return getPlayer().map(p -> p.hasPermission(permission)).orElse(false);
    }

    public void tick() {
        if (lastTickTime == 0) {
            lastTickTime = System.currentTimeMillis();
            return;
        }
        Optional<Player> optionalPlayer = getPlayer();
        if (!optionalPlayer.isPresent()) {
            return;
        }
        if (!shouldAddTime(optionalPlayer.get())) {
            return;
        }
        LocalDate today = Sponge.getServiceManager().provideUnchecked(DailyBonusTimeService.class).getToday();
        if (!today.equals(this.today)) {
            LocalDate date = this.today;
            long online = onlineTimeToday;
            CompletableFuture.runAsync(() -> dailyBonus.getStorage().setOnlineTime(getUniqueId(), date, online))
                .whenComplete(UtilMethods.handleException(dailyBonus.getLogger(), "Exception on save online time data."));
            this.today = today;
            onlineTimeToday = 0;
            dailyBonus.getPlayerDataManager().updatePlayerData(optionalPlayer.get())
                .whenComplete(UtilMethods.handleException(dailyBonus.getLogger(), "Exception on update player data."));
        }
        long now = System.currentTimeMillis();
        long playTime = Math.max(0, now - lastTickTime);
        lastTickTime = now;
        onlineTimeTotal += playTime;
        onlineTimeToday += playTime;
    }
}
