package dev.ginyai.dailybonus.i18n;

import dev.ginyai.dailybonus.DailyBonusMain;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.api.asset.Asset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class I18n<Text> {

    private final DailyBonusMain dailyBonus;
    private final Path dataDir;
    private final Function<String, Optional<Asset>> resourceGetter;
    private final Function<String, Text> textParser;

    private Function<String, String> rawMessageGetter;

    public I18n(DailyBonusMain dailyBonus, Path dataDir, Function<String, Optional<Asset>> resourceGetter, Function<String, Text> textParser) {
        this.dailyBonus = dailyBonus;
        this.dataDir = dataDir;
        this.resourceGetter = resourceGetter;
        this.textParser = textParser;
    }

    public void reload(Locale locale, boolean saveDefaultFile) throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        Path msgFile = dataDir.resolve("i18n.yml");
        String loc = locale.toLanguageTag().toLowerCase(Locale.ROOT).replace('-', '_');
        LinkedList<ConfigurationNode> usableSettings = new LinkedList<>();
        if (Files.exists(msgFile)) {
            usableSettings.add(HoconConfigurationLoader.builder().setPath(msgFile).build().load());
            saveDefaultFile = false;
        }
        Optional<Asset> optionalAsset = resourceGetter.apply("i18n/" + loc + ".conf");
        if (optionalAsset.isPresent()) {
            usableSettings.add(HoconConfigurationLoader.builder().setURL(optionalAsset.get().getUrl()).build().load());
            if (saveDefaultFile) {
                optionalAsset.get().copyToFile(msgFile);
            }
        }
        if (!loc.equals("en_us")) {
            Optional<Asset> optionalAssetUs = resourceGetter.apply("i18n/en_us.conf");
            if (optionalAssetUs.isPresent()) {
                usableSettings.add(HoconConfigurationLoader.builder().setURL(optionalAssetUs.get().getUrl()).build().load());
                if (saveDefaultFile) {
                    optionalAssetUs.get().copyToFile(msgFile);
                }
            }
        }
        if (usableSettings.isEmpty()) {
            throw new IllegalStateException("Unable to load any locale file.");
        }
        rawMessageGetter = key -> {
            Object[] k = key.split("\\.");
            for (ConfigurationNode node : usableSettings) {
                String s = node.getNode(k).getString();
                if (s != null) {
                    return s;
                }
            }
            return null;
        };
    }

    public Text translateToLocal(String key) {
        return translateToLocal(key, Collections.emptyMap());
    }

    public Text translateToLocal(String key, Map<String, ?> args) {
        String s = rawMessageGetter.apply(key);
        if (s == null) {
            dailyBonus.getLogger().warn("Unable to find msg for key: {}", key);
            s = key;
        }
        return replacePlaceholders(s, args);
    }

    private Text replacePlaceholders(String s, Map<String, ?> args) {
        return textParser.apply(dailyBonus.getPlaceholders().replacePlaceholders(s, args));
    }
}
