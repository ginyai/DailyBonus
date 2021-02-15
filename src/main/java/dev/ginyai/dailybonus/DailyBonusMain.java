package dev.ginyai.dailybonus;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import dev.ginyai.dailybonus.api.DailyBonusService;
import dev.ginyai.dailybonus.api.bonus.BonusEntry;
import dev.ginyai.dailybonus.api.bonus.BonusRequirement;
import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.data.IStorage;
import dev.ginyai.dailybonus.api.time.DailyBonusTimeService;
import dev.ginyai.dailybonus.api.time.TimeCycle;
import dev.ginyai.dailybonus.api.time.TimeRange;
import dev.ginyai.dailybonus.api.view.DailyBonusViewManager;
import dev.ginyai.dailybonus.bonus.AbstractBonusSet;
import dev.ginyai.dailybonus.bonus.BonusEntries;
import dev.ginyai.dailybonus.bonus.BonusEntryCommands;
import dev.ginyai.dailybonus.bonus.BonusEntrySign;
import dev.ginyai.dailybonus.bonus.BonusRequirementOnlineTimeToday;
import dev.ginyai.dailybonus.bonus.BonusRequirementOnlineTimeTotal;
import dev.ginyai.dailybonus.bonus.BonusRequirementPermission;
import dev.ginyai.dailybonus.bonus.BonusRequirementSignCount;
import dev.ginyai.dailybonus.bonus.BonusRequirements;
import dev.ginyai.dailybonus.command.CommandBonus;
import dev.ginyai.dailybonus.command.CommandListParms;
import dev.ginyai.dailybonus.command.CommandOpen;
import dev.ginyai.dailybonus.command.CommandReload;
import dev.ginyai.dailybonus.command.CommandSign;
import dev.ginyai.dailybonus.command.TreeCommand;
import dev.ginyai.dailybonus.config.ChestViewDisplaySettings;
import dev.ginyai.dailybonus.config.ConfigLoadingTracker;
import dev.ginyai.dailybonus.config.GeneralSettings;
import dev.ginyai.dailybonus.config.StorageSettings;
import dev.ginyai.dailybonus.config.serializers.TypeSerializerBonusEntry;
import dev.ginyai.dailybonus.config.serializers.TypeSerializerBonusRequirement;
import dev.ginyai.dailybonus.config.serializers.TypeSerializerBonusSet;
import dev.ginyai.dailybonus.config.serializers.TypeSerializerChestElement;
import dev.ginyai.dailybonus.config.serializers.TypeSerializerDisplaySettings;
import dev.ginyai.dailybonus.config.serializers.TypeSerializerSignGroup;
import dev.ginyai.dailybonus.data.SimpleDataManager;
import dev.ginyai.dailybonus.data.StorageManager;
import dev.ginyai.dailybonus.data.TrackedPlayer;
import dev.ginyai.dailybonus.i18n.I18n;
import dev.ginyai.dailybonus.placeholder.DailyBonusPlaceholders;
import dev.ginyai.dailybonus.time.SimpleTimeService;
import dev.ginyai.dailybonus.util.ConfigUtils;
import dev.ginyai.dailybonus.view.chest.ChestElement;
import dev.ginyai.dailybonus.view.chest.ChestViewManager;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class DailyBonusMain implements DailyBonusService, DailyBonusTimeService {

    private Logger logger = LoggerFactory.getLogger("DailyBonus");

    private final DailyBonusPlugin plugin;

    private Path dataDir;
    private Path generalConfigPath;
    private Function<String, Text> textParser;

    private SpongeExecutorService syncExecutor;
    private IStorage storage;
    private StorageManager storageManager = new StorageManager();
    private SimpleDataManager playerDataManager;
    private ChestViewManager viewManager;
    private SimpleTimeService simpleTimeService = new SimpleTimeService(ZoneId.systemDefault(), LocalTime.of(4, 0), DayOfWeek.MONDAY);
    private I18n<Text> i18n;
    private DailyBonusPlaceholders placeholders = new DailyBonusPlaceholders(this);
    private GeneralSettings generalSettings = new GeneralSettings();
    private Map<String, SignGroup> signGroupMap = Collections.emptyMap();
    private Map<String, BonusSet> bonusSetMap = Collections.emptyMap();
    private Map<String, ChestViewDisplaySettings> displaySettingsMap = Collections.emptyMap();

    private TreeCommand rootCommand;

    private BonusEntries bonusEntries = new BonusEntries();
    private BonusRequirements bonusRequirements = new BonusRequirements();

    private ConfigurationOptions options = ConfigurationOptions.defaults();

    public DailyBonusMain(DailyBonusPlugin plugin) {
        this.plugin = plugin;
    }


    public void onPreInit(Path dataDir, Function<String, Text> textParser) {
        logger.debug("onPreInit");
        Sponge.getServiceManager().setProvider(plugin, DailyBonusTimeService.class, this);
        Sponge.getServiceManager().setProvider(plugin, DailyBonusService.class, this);
        this.dataDir = Objects.requireNonNull(dataDir);
        this.textParser = Objects.requireNonNull(textParser);
        generalConfigPath = dataDir.resolve("DailyBonusGeneral.conf");
        syncExecutor = Sponge.getScheduler().createSyncExecutor(plugin);

        i18n = new I18n<>(this, dataDir, s -> Sponge.getAssetManager().getAsset(plugin,s), textParser);
        try {
            i18n.reload(Locale.US.toLanguageTag(), false);
        } catch (Exception e) {
            logger.error("Exception on load local file.", e);
        }

        playerDataManager = new SimpleDataManager(this, TrackedPlayer::new);
        viewManager = new ChestViewManager(this, () -> displaySettingsMap);

        //todo: registry elements
        TypeSerializerCollection serializerCollection = TypeSerializers.getDefaultSerializers().newChild();
        serializerCollection.registerType(TypeToken.of(SignGroup.class), new TypeSerializerSignGroup(this));
        serializerCollection.registerType(TypeToken.of(BonusEntry.class), new TypeSerializerBonusEntry(bonusEntries));
        serializerCollection.registerType(TypeToken.of(BonusRequirement.class), new TypeSerializerBonusRequirement(bonusRequirements));
        serializerCollection.registerType(TypeToken.of(BonusSet.class), new TypeSerializerBonusSet(this));
        serializerCollection.registerType(TypeToken.of(ChestElement.class), new TypeSerializerChestElement(this));
        serializerCollection.registerType(TypeToken.of(ChestViewDisplaySettings.class), new TypeSerializerDisplaySettings(this));
        options = options.setSerializers(serializerCollection);

        //todo: api ?
        bonusEntries.registry("command", node -> new BonusEntryCommands(this, node.getNode("commands").getList(TypeToken.of(String.class))));
        bonusEntries.registry("sign", node -> new BonusEntrySign(Objects.requireNonNull(signGroupMap.get(ConfigUtils.readNonnull(node.getNode("sign-group"), ConfigurationNode::getString)))));

        //todo: api ?
        bonusRequirements.registry("onlinetimetoday", node -> new BonusRequirementOnlineTimeToday(this, ConfigUtils.readNonnull(node.getNode("online-time"), ConfigUtils::readTimespan)));
        bonusRequirements.registry("onlinetimetotal", node -> new BonusRequirementOnlineTimeTotal(this, ConfigUtils.readNonnull(node.getNode("online-time"), ConfigUtils::readTimespan)));
        bonusRequirements.registry("signcount", node -> new BonusRequirementSignCount(this,
            Objects.requireNonNull(signGroupMap.get(ConfigUtils.readNonnull(node.getNode("sign-group"), ConfigurationNode::getString))),
            ConfigUtils.readNonnull(node.getNode("count"), ConfigurationNode::getInt)
        ));
        bonusRequirements.registry("permission", node -> new BonusRequirementPermission(this,
            ConfigUtils.readNonnull(node.getNode("permission"), ConfigurationNode::getString),
            //todo: use text parser ?
            ConfigUtils.readNonnull(node.getNode("display"), n -> n.getValue(TypeToken.of(Text.class)))
        ));

        rootCommand = new TreeCommand("root", this);
        rootCommand.addHelp();
        rootCommand.addChild(new CommandReload(this));
        rootCommand.addChild(new CommandOpen(this));
        rootCommand.addChild(new CommandSign(this));
        rootCommand.addChild(new CommandBonus(this));
        rootCommand.addChild(new CommandListParms(this));
    }

    public void onInit() {
        logger.debug("onInit");
        try {
            logger.info("Loading config...");
            reload();
            logger.info("Config Loaded.");
        } catch (Exception e) {
            getLogger().error("Exception on load config.", e);
        }
    }

    public void onPostInit() {
        logger.debug("onPostInit");

    }

    public void onClose() {
        logger.debug("onClose");
        getPlayerDataManager().onClose();
        storageManager.onClose();
    }

    public void onPlayerJoin(Player player) {
        playerDataManager.onPlayerJoin(player)
            .whenCompleteAsync((trackedPlayer, t) ->
                trackedPlayer.getPlayer().ifPresent(player1 ->
                    bonusSetMap.values().stream()
                        .filter(AbstractBonusSet.class::isInstance)
                        .map(AbstractBonusSet.class::cast)
                        .filter(AbstractBonusSet::isAutoComplete)
                        .forEach(bonusSet -> checkAutoComplete(bonusSet, player1, trackedPlayer)))
                , syncExecutor);
    }

    public void tick() {
        playerDataManager.tick();
    }

    private void checkAutoComplete(AbstractBonusSet bonusSet, Player player, TrackedPlayer playerData) {
        if (playerData.isReceived(bonusSet)) {
            return;
        }
        if (!bonusSet.getRequirements().stream().allMatch(r -> r.check(playerData))) {
            return;
        }
        bonusSet.give(player);
    }

    public void reload() throws Exception {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        boolean configExits = Files.exists(generalConfigPath);
        // general settings
        ConfigurationLoader<CommentedConfigurationNode> generalConfigLoader = HoconConfigurationLoader.builder().setPath(generalConfigPath).build();
        CommentedConfigurationNode rootNode = generalConfigLoader.load(options);
        generalSettings = rootNode.getNode("DailyBonusGeneral").getValue(TypeToken.of(GeneralSettings.class), generalSettings);
        StorageSettings storageSettings = generalSettings.getStorageSettings();
        i18n.reload(generalSettings.getLocale(), false);
        storage = storageManager.createStorage(storageSettings, dataDir);
        storage.setup();
        //todo: zone settings
        simpleTimeService.updateSettings(ZoneId.systemDefault(), LocalTime.parse(generalSettings.getStartOfDay()), generalSettings.getStartOfWeek());
        if (!configExits) {
            rootNode.getNode("DailyBonusGeneral").setValue(TypeToken.of(GeneralSettings.class), generalSettings);
            generalConfigLoader.save(rootNode);
        }

        ImmutableMap.Builder<String, SignGroup> signGroupBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, BonusSet> bonusSetBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, ChestViewDisplaySettings> displaySettingsBuilder = ImmutableMap.builder();

        for (String dirString: generalSettings.getSettingsDir()) {
            try {
                Path dirPath = Paths.get(dirString.replace("%data_dir%", dataDir.toString()));
                ConfigLoadingTracker.INSTANCE.loadDir(dirPath);
                if (!Files.exists(dirPath)) {
                    if (dirPath.toRealPath().startsWith(dataDir.toRealPath())) {
                        Files.createDirectories(dirPath);
                    } else {
                        continue;
                    }
                }
                Files.walk(dirPath, 1)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).startsWith(".conf"))
                    .forEach(path -> {
                        try {
                            ConfigLoadingTracker.INSTANCE.loadFile(path);
                            ConfigurationLoader<CommentedConfigurationNode> loader1 = HoconConfigurationLoader.builder().setPath(path).build();
                            ConfigurationNode node = loader1.load(options);
                            List<SignGroup> signGroupList = node.getNode("DailyBonus", "SignGroup").getList(TypeToken.of(SignGroup.class));
                            signGroupList.forEach(signGroup -> signGroupBuilder.put(signGroup.getId(), signGroup));
                            List<BonusSet> bonusSetList = node.getNode("DailyBonus", "BonusSet").getList(TypeToken.of(BonusSet.class));
                            bonusSetList.forEach(bonusSet -> bonusSetBuilder.put(bonusSet.getId(), bonusSet));
                            bonusSetMap = bonusSetList.stream().collect(ImmutableMap.toImmutableMap(BonusSet::getId, Function.identity()));
                            Map<String, ChestViewDisplaySettings> displaySettingsMap = ConfigUtils.readMap(node.getNode("DailyBonus", "ChestView"), node1 -> node1.getValue(TypeToken.of(ChestViewDisplaySettings.class)));
                            displaySettingsMap.forEach((name, settings) -> {
                                displaySettingsBuilder.put(ConfigLoadingTracker.INSTANCE.getCurPrefix() + "." +name, settings);
                            });
                        } catch (Exception e) {
                            logger.error("Exception on loading config file : " + path, e);
                        }
                    });
            } catch (Exception e) {
                logger.error("Exception on loading configs in dir: " + dirString, e);
            }
            signGroupMap = signGroupBuilder.orderEntriesByValue(Comparator.comparing(SignGroup::getId)).build();
            bonusSetMap = bonusSetBuilder.orderEntriesByValue(Comparator.comparing(BonusSet::getId)).build();
            displaySettingsMap = displaySettingsBuilder.build();
        }
        playerDataManager.onReload();
    }

    public Logger getLogger() {
        return logger;
    }

    public I18n<Text> getI18n() {
        return i18n;
    }

    public DailyBonusPlaceholders getPlaceholders() {
        return placeholders;
    }

    public Function<String, Text> getTextParser() {
        return textParser;
    }

    public TreeCommand getRootCommand() {
        return rootCommand;
    }

    public BonusEntries getBonusEntries() {
        return bonusEntries;
    }

    public BonusRequirements getBonusRequirements() {
        return bonusRequirements;
    }

    @Override
    public IStorage getStorage() {
        return storage;
    }

    @Override
    public Optional<SignGroup> getSignGroupById(String id) {
        return Optional.ofNullable(signGroupMap.get(id));
    }

    @Override
    public Collection<? extends SignGroup> getSignGroups() {
        return signGroupMap.values();
    }

    @Override
    public Optional<BonusSet> getBonusSetById(String id) {
        return Optional.ofNullable(bonusSetMap.get(id));
    }

    @Override
    public Collection<? extends BonusSet> getBonusSets() {
        return bonusSetMap.values();
    }

    @Override
    public DailyBonusViewManager getViewManager() {
        return viewManager;
    }

    @Override
    public SimpleDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    @Override
    public ZoneId getTimeZone() {
        return simpleTimeService.getTimeZone();
    }

    @Override
    public LocalTime getStartOfDay() {
        return simpleTimeService.getStartOfDay();
    }

    @Override
    public DayOfWeek getStartOfWeek() {
        return simpleTimeService.getStartOfWeek();
    }

    @Override
    public LocalDate getDate(Instant instant) {
        return simpleTimeService.getDate(instant);
    }

    @Override
    public LocalDate getDate(LocalDateTime dateTime) {
        return simpleTimeService.getDate(dateTime);
    }

    @Override
    public TimeRange<LocalDateTime> getCycleAt(TimeCycle cycle, LocalDateTime at) {
        return simpleTimeService.getCycleAt(cycle, at);
    }

    @Override
    public Instant toInstance(LocalDateTime dateTime) {
        return simpleTimeService.toInstance(dateTime);
    }

    public Executor getSyncExecutor() {
        return syncExecutor;
    }

    public Map<String, SignGroup> getSignGroupMap() {
        return signGroupMap;
    }

    public Map<String, BonusSet> getBonusSetMap() {
        return bonusSetMap;
    }

    public void dispatchConsoleCommand(String cmd) {
        CommandSource console = Sponge.getServer().getConsole();
        Sponge.getCommandManager().process(console, cmd);
    }

    public DailyBonusPlugin getPlugin() {
        return plugin;
    }
}
