package dev.ginyai.dailybonus.util;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.DailyBonusPlugin;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ReloadFailException extends Exception {
    private final boolean terminated;
    private final int errorsCount;
    private final int warningsCount;

    ReloadFailException(LoadingIssuesTracker tracker) {
        this(tracker.isTerminated(), (int) tracker.countErrors(), (int)tracker.countWarnings());
    }

    ReloadFailException(LoadingIssuesTracker tracker, Throwable cause) {
        this(tracker.isTerminated(), (int) tracker.countErrors(), (int)tracker.countWarnings(), cause);
    }

    public ReloadFailException(boolean terminated, int errorsCount, int warningsCount) {
        super(getMsg(terminated, errorsCount, warningsCount));
        this.terminated = terminated;
        this.errorsCount = errorsCount;
        this.warningsCount = warningsCount;
    }


    public ReloadFailException(boolean terminated, int errorsCount, int warningsCount, Throwable cause) {
        super(getMsg(terminated, errorsCount, warningsCount), cause);
        this.terminated = terminated;
        this.errorsCount = errorsCount;
        this.warningsCount = warningsCount;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public int getErrorsCount() {
        return errorsCount;
    }

    public int getWarningsCount() {
        return warningsCount;
    }

    private static String getMsg(boolean terminated, int errorsCount, int warningsCount) {
        if (terminated) {
            return TextSerializers.LEGACY_FORMATTING_CODE.serialize(
                DailyBonusPlugin.getInstance().getDailyBonus()
                    .getI18n().translateToLocal("loading.terminated", ImmutableMap.of(
                        "error", errorsCount,
                        "warn", warningsCount
                    ))
            ) + "§r";
//            return String.format("Unable to complain config loading. %d Error(s), %d Warning(s)", errorsCount, warningsCount);
        } else {
            return TextSerializers.LEGACY_FORMATTING_CODE.serialize(
                DailyBonusPlugin.getInstance().getDailyBonus()
                    .getI18n().translateToLocal("loading.error", ImmutableMap.of(
                    "error", errorsCount,
                    "warn", warningsCount
                ))
            ) + "§r";
//            return String.format("Config loaded, but several issues happened. %d Error(s), %d Warning(s)", errorsCount, warningsCount);
        }
    }
}
