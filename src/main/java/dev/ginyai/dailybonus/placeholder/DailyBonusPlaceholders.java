package dev.ginyai.dailybonus.placeholder;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyBonusPlaceholders {


    protected static Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([0-9a-zA-Z_-]+)%");

    private final DailyBonusMain dailyBonus;

    // TODO: 2021/2/15 Return Text ?
    public DailyBonusPlaceholders(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public String replacePlaceholders(String sIn, Map<String, ?> map) {
        return replacePlaceholders(sIn, new MapPlaceholderContainer(map));
    }

    public String replacePlaceholders(String sIn, IPlaceholderContainer container) {
        return replacePlaceholders(sIn, key -> toString(container.parsePlaceholder(key.split("_"))));
    }

    public String replacePlaceholders(String sIn, Function<String, String> function) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(sIn);
        boolean result = matcher.find();
        if (result) {
            StringBuilder sb = new StringBuilder();
            int lastAppendPosition = 0;
            do {
                String replacement = function.apply(matcher.group(1));
                if (replacement != null) {
                    sb.append(sIn, lastAppendPosition, matcher.start());
                    sb.append(replacement);
                } else {
                    sb.append(sIn, lastAppendPosition, matcher.end());
                }
                lastAppendPosition = matcher.end();
                result = matcher.find();
            } while (result);
            sb.append(sIn, lastAppendPosition, sIn.length());
            return sb.toString();
        }
        return sIn;
    }


    public String replaceCommandPlaceholders(String sIm, Player player) {
        return replacePlaceholders(sIm, ImmutableMap.of("player", dailyBonus.getPlayerDataManager().getOrCreatePlayerData(player)));
    }

    public String replacePlaceholders(String key, PlayerData player) {
        return replacePlaceholders(key, (IPlaceholderContainer)player);
    }

    public Object requestPlaceholder(String key, PlayerData playerData) {
        return playerData.parsePlaceholder(key.toLowerCase(Locale.ROOT).split("_"));
    }

    private static String toString(Object o) {
        if (o instanceof Text) {
            return TextSerializers.LEGACY_FORMATTING_CODE.serialize((Text) o);
        } else if (o instanceof TextRepresentable) {
            return TextSerializers.LEGACY_FORMATTING_CODE.serialize(((TextRepresentable) o).toText());
        } else {
            return String.valueOf(o);
        }
    }

}
