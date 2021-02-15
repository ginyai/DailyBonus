package dev.ginyai.dailybonus.command;

import dev.ginyai.dailybonus.DailyBonusMain;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

@NonnullByDefault
public class CommandHelp extends AbstractCommand {
    private final TreeCommand parent;

    public CommandHelp(DailyBonusMain dailyBonus, TreeCommand parent) {
        super("help", dailyBonus);
        this.parent = parent;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ICommand command = args.requireOne("command");
        src.sendMessage(Text.of(command.getName()));
        src.sendMessage(command.toCallable().getHelp(src).orElseGet(() -> command.toCallable().getUsage(src)));
        return CommandResult.success();
    }

    @Override
    protected CommandElement getArgs() {
        return GenericArguments.optional(new ArgChildCommand(Text.of("command"), parent), this);
    }
}
