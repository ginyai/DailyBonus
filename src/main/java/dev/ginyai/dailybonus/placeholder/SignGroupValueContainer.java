package dev.ginyai.dailybonus.placeholder;

import dev.ginyai.dailybonus.api.bonus.SignGroup;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;

import javax.annotation.Nullable;
import java.util.Locale;

public class SignGroupValueContainer implements IPlaceholderContainer {
    private final PlayerData playerData;
    private final SignGroup signGroup;

    public SignGroupValueContainer(PlayerData playerData, SignGroup signGroup) {
        this.playerData = playerData;
        this.signGroup = signGroup;
    }

    @Nullable
    @Override
    public Object parsePlaceholder(String... args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "count":
                    return playerData.getPunchedDays(signGroup);
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public void visitData(IPlaceholderContainer.PlaceholderVisitor visitor) {
        visitor.visit("count", playerData.getPunchedDays(signGroup));
    }
}
