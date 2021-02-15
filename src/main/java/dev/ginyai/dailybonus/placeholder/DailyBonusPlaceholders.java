package dev.ginyai.dailybonus.placeholder;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DailyBonusPlaceholders {


    protected static Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([0-9a-zA-Z_-]+)%");

    private final DailyBonusMain dailyBonus;

    public DailyBonusPlaceholders(DailyBonusMain dailyBonus) {
        this.dailyBonus = dailyBonus;
    }

    public String replacePlaceholders(String sIn, Map<String, ?> map) {
        return replacePlaceholders(sIn, new MapPlaceholderContainer(map));
    }

    public String replacePlaceholders(String sIn, IPlaceholderContainer container) {
        //todo: better to string
        return replacePlaceholders(sIn, key -> String.valueOf(container.parsePlaceholder(key.split("_"))));
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

}
