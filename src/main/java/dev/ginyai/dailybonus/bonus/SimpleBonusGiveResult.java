package dev.ginyai.dailybonus.bonus;

import dev.ginyai.dailybonus.api.bonus.BonusSet;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SimpleBonusGiveResult implements BonusSet.GiveResult {

    private final boolean isSuccessful;
    private final Text msg;

    public SimpleBonusGiveResult(boolean isSuccessful, Text msg) {
        this.isSuccessful = isSuccessful;
        this.msg = msg;
    }

    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public Optional<Text> getFailMessage() {
        return Optional.ofNullable(msg);
    }
}
