package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

@NonnullByDefault
public class CommandReload extends AbstractCommand {

    public CommandReload(DailyBonusMain dailyBonus) {
        super("reload", dailyBonus);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            dailyBonus.reload();
            src.sendMessage(getTranslation("success"));
            return CommandResult.success();
        } catch (Exception e) {
            dailyBonus.getLogger().error("Exception on reload.", e);
            throw new CommandException(getTranslation("failed"));
        }
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.none();
    }
}
