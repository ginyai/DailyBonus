package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@NonnullByDefault
public class CommandReload extends AbstractCommand {

    public CommandReload(DailyBonusMain dailyBonus) {
        super("reload", dailyBonus);
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        try {
            dailyBonus.reload();
            source.sendMessage(getTranslation("success"));
            return CommandResult.success();
        } catch (Exception e) {
            dailyBonus.getLogger().error("Exception on reload.", e);
            throw new CommandException(getTranslation("failed"));
        }
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return Collections.emptyList();
    }
}
