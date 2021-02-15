package dev.ginyai.dailybonus.command;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

@NonnullByDefault
public abstract class AbstractCommand implements ICommand {

    private final String name;
    protected final DailyBonusMain dailyBonus;
    @Nullable
    protected CommandCallable callable;

    protected AbstractCommand(String name, DailyBonusMain dailyBonus) {
        this.name = name;
        this.dailyBonus = dailyBonus;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract CommandResult execute(CommandSource src, CommandContext args) throws CommandException;

    protected abstract CommandElement getArgs();

    @Override
    public CommandCallable toCallable() {
        if (callable == null) {
            callable = CommandSpec.builder()
                .permission(getPermissionString("base"))
                .arguments(getArgs())
                .executor(this)
                .build()
            ;
        }
        return callable;
    }

    protected String getPermissionString(String s) {
        return "dailybonus.command." + getName().toLowerCase(Locale.ROOT) + "." + s;
    }

    protected String getTranslationKey() {
        return "command."+getName()+".";
    }

    public Text getTranslation(String key, Map<String, ?> args) {
        return dailyBonus.getI18n().translateToLocal(getTranslationKey() + key, args);
    }

    public Text getTranslation(String key) {
        return dailyBonus.getI18n().translateToLocal(getTranslationKey() + key, ImmutableMap.of());
    }

    public Text getTranslation(String key, String k1, Object v1) {
        return dailyBonus.getI18n().translateToLocal(getTranslationKey() + key, ImmutableMap.of(k1, v1));
    }

    public Text getTranslation(String key, String k1, Object v1, String k2, Object v2) {
        return dailyBonus.getI18n().translateToLocal(getTranslationKey() + key, ImmutableMap.of(k1, v1, k2, v2));
    }

    public Text getTranslation(String key, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return dailyBonus.getI18n().translateToLocal(getTranslationKey() + key, ImmutableMap.of(k1, v1, k2, v2, k3, v3));
    }

    public Text getTranslation(String key, String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        return dailyBonus.getI18n().translateToLocal(getTranslationKey() + key, ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4));
    }

}
