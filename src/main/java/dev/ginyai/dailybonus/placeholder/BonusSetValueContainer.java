package dev.ginyai.dailybonus.placeholder;

import dev.ginyai.dailybonus.api.bonus.BonusSet;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.api.placeholder.IPlaceholderContainer;

import javax.annotation.Nullable;
import java.util.Locale;

// TODO: 2021/3/5  BonusRequirements ?
public class BonusSetValueContainer implements IPlaceholderContainer {
    private final PlayerData playerData;
    private final BonusSet bonusSet;

    public BonusSetValueContainer(PlayerData playerData, BonusSet bonusSet) {
        this.playerData = playerData;
        this.bonusSet = bonusSet;
    }

    @Nullable
    @Override
    public Object parsePlaceholder(String... args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "received":
                    return playerData.isReceived(bonusSet);
                case "id":
                    return bonusSet.getId();
                case "display":
                    return bonusSet.getDisplay();
            }
        }
        return null;
    }

    @Override
    public void visitData(IPlaceholderContainer.PlaceholderVisitor visitor) {
        visitor.visit("received", playerData.isReceived(bonusSet));
        visitor.visit("id", bonusSet.getId());
        visitor.visit("display", bonusSet.getDisplay());
    }
}
