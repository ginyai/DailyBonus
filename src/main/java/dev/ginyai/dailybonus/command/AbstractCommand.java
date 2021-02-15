package dev.ginyai.dailybonus.command;

import com.google.common.collect.ImmutableMap;
import dev.ginyai.dailybonus.DailyBonusMain;
import dev.ginyai.dailybonus.api.data.PlayerData;
import dev.ginyai.dailybonus.placeholder.PlaceholderVisitor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@NonnullByDefault
public abstract class AbstractCommand implements ICommand {

    private final String name;
    protected final DailyBonusMain dailyBonus;

    protected AbstractCommand(String name, DailyBonusMain dailyBonus) {
        this.name = name;
        this.dailyBonus = dailyBonus;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return getTranslation("usage");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    protected String getPermissionString(String s) {
        return "dailybonus.command." + getName().toLowerCase(Locale.ROOT) + "." + s;
    }

    @Override
    public abstract CommandResult process(CommandSource source, String arguments) throws CommandException;

    @Override
    public abstract List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException;

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission(getPermissionString("base"));
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
